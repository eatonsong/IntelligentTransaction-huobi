package com.soft.trade;

import com.huobi.client.model.Order;
import com.huobi.client.model.enums.OrderState;
import com.soft.mapper.TransEventMapper;
import com.soft.mapper.TransPrepareOrderMapper;
import com.soft.po.TransEvent;
import com.soft.po.TransPrepareOrder;
import com.soft.rpc.rest.sync.TransAccountSync;
import com.soft.rpc.rest.sync.TransOrderSync;
import com.soft.rpc.rest.sync.TransTarde;
import com.soft.support.TransCommon;
import com.soft.support.TransConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScanTask {

    protected static final Logger logger = LoggerFactory.getLogger(ScanTask.class);

    //接口访问

    @Autowired
    private TransAccountSync transAccountSync;
    @Autowired
    private TransOrderSync transOrderSync;
    @Autowired
    private TransTarde transTarde;


    //数据访问
    @Resource
    private TransEventMapper eventMapper;

    @Resource
    private TransPrepareOrderMapper prepareOrderMapper;

    //工具访问
    @Autowired
    private AsyncTask asyncTask;



    public void start(){
        TransEvent param = new TransEvent();
        param.setState(TransConst.EventState.doing);
        //查出全部未完成事件
        List<TransEvent> events = eventMapper.selectAll(param);
        for(TransEvent e:events){
            //查询事件关联的全部预订单
            TransPrepareOrder transPrepareOrder = new TransPrepareOrder();
            transPrepareOrder.setEventId(e.getEventId());
            List<TransPrepareOrder> prepareOrders = prepareOrderMapper.selectAll(transPrepareOrder);
            //统计event下挂的预订单数量
            int count = 0;
            List filters = prepareOrders.stream().filter(f->f.getState().equals(TransConst.PrepareOrderState.doing)||f.getState().equals(TransConst.PrepareOrderState.init)).collect(Collectors.toList());
            logger.info("未完成的预订单数量:eventId-{},{}",e.getEventId(), filters.size());
            for(TransPrepareOrder pre:prepareOrders){
                //如果预订单是初始化状态则进行上架操作
                switch (pre.getState()){
                    //初始化状态则上架订单
                    case TransConst.PrepareOrderState.init:
                        //初始化的订单超过10分钟后设置为无效
                        long min = TransCommon.getDistanceTimes(pre.getCreateTime());
                        if(min >= 10){
                            pre.setState(TransConst.PrepareOrderState.cancel);
                            logger.info("prepare-orderId:{},createTime:{},初始化超时取消订单",pre.getPrepareOrderId(),pre.getCreateTime());
                            prepareOrderMapper.updateByPrimaryKey(pre);
                            break;
                        }

                        //如果余额足够则上架
                        boolean isRemain = transAccountSync.isRemainFull(pre.getSymbol(),pre.getType(),pre.getAmount(),pre.getPrice());

                        if(!isRemain){
                            logger.warn("prepare-orderId:{},金额不足不能上架",pre.getPrepareOrderId());
                        }else{
                            BigDecimal orderPrice = new BigDecimal(pre.getPrice());
                            //上架之前检验成交价格如果价格更高则按照高价出售
                            try{
                                BigDecimal price = transTarde.getLastTrade(pre.getSymbol());
                                if(pre.getType().contains("BUY")){
                                    //如果是购买的话线上价格更低以线上为主
                                    if(price.compareTo(orderPrice) == -1){
                                        orderPrice = price;
                                    }
                                }else{
                                    //如果是出售的话线上价格更高以线上为主
                                    if(price.compareTo(orderPrice) == 1){
                                        orderPrice = price;
                                    }
                                }
                                logger.info("symbol:{},type：{},线上价格:{},预订单价格:{} ,最终上线价格:{}",pre.getSymbol(),pre.getType(),price,pre.getPrice(),orderPrice);
                            }catch (Exception e1){
                                logger.error("上架之前检验成交价格过程中发生异常:{}",e1);
                            }
                            long orderId = transOrderSync.createOrder(pre.getSymbol(),new BigDecimal(pre.getAmount()),orderPrice,pre.getType());
                            logger.info("prepare-orderId:{},可以上架,symbol:{},amount:{},price:{},type:{}",pre.getPrepareOrderId(),pre.getSymbol(),pre.getAmount(),orderPrice,pre.getType());
                            pre.setHuoOrderId(orderId+"");
                            pre.setState(TransConst.PrepareOrderState.doing);
                            prepareOrderMapper.updateByPrimaryKey(pre);
                        }
                        break;
                        //处理中的预订单则与线上数据同步
                    case TransConst.PrepareOrderState.doing:
                        if(pre.getHuoOrderId() != null){
                            Order huo = transOrderSync.getOrder(pre.getSymbol(),Long.parseLong(pre.getHuoOrderId()));
                            //订单取消或者订单完成均关闭订单
                            if(huo.getState() == OrderState.FILLED || huo.getState() == OrderState.CANCELED || huo.getState() == OrderState.PARTIALCANCELED){
                                pre.setState(TransConst.PrepareOrderState.end);
                                logger.info("prepare-orderId:{},交易完成关闭预订单",pre.getPrepareOrderId());
                                prepareOrderMapper.updateByPrimaryKey(pre);
                                //预订单完成-插入订单
                                asyncTask.saveEndOrderSync(pre);
                            }else{
                                //订单未完成情况下，判断事件是否超过1小时，如果超过则取消订单
                                long min1 = TransCommon.getDistanceTimes(pre.getCreateTime());
                                if(min1 >= 50){
                                    pre.setState(TransConst.PrepareOrderState.cancel);
                                    //取消订单
                                    transOrderSync.cancelOrder(pre.getSymbol(),Long.parseLong(pre.getHuoOrderId()));
                                    logger.info("prepare-orderId:{},createTime:{},上架中超时取消订单",pre.getPrepareOrderId(),pre.getCreateTime());
                                    prepareOrderMapper.updateByPrimaryKey(pre);
                                    //预订单取消-插入订单
                                    asyncTask.saveEndOrderSync(pre);
                                    break;
                                }
                            }

                        }
                        break;
                        //结束的预订单则计算是否可以关闭预订单
                    case TransConst.PrepareOrderState.end:
                        count++;
                        break;
                        //取消的预订单
                    case TransConst.PrepareOrderState.cancel:
                        count++;
                        break;
                }
            }
            if(count == prepareOrders.size()){ //有可能只有两种事件
                //结束事件
                e.setState(TransConst.EventState.end);
                eventMapper.updateByPrimaryKey(e);
                logger.info("eventId-{}:事件结束",e.getEventId());
                // 计算总盈亏
                asyncTask.handleEventSettle(e.getEventId());
            }




        }


    }


}
