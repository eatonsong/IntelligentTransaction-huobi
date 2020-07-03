package com.soft.trade;

import com.huobi.client.model.Order;
import com.huobi.client.model.enums.OrderState;
import com.soft.mapper.TransEventMapper;
import com.soft.mapper.TransOrderMapper;
import com.soft.mapper.TransSettleMapper;
import com.soft.po.TransEvent;
import com.soft.po.TransOrder;
import com.soft.po.TransPrepareOrder;
import com.soft.po.TransSettle;
import com.soft.rpc.rest.sync.TransOrderSync;
import com.soft.rpc.rest.sync.TransTarde;
import com.soft.support.NamedThreadFactory;
import com.soft.support.SymbolEnum;
import com.soft.support.TransCommon;
import com.soft.support.TransConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

@Component
public class AsyncTask {
    protected static final Logger logger = LoggerFactory.getLogger(AsyncTask.class);

    @Resource
    private TransOrderMapper transOrderMapper;
    @Resource
    private TransEventMapper transEventMapper;
    @Resource
    private TransSettleMapper transSettleMapper;

    @Autowired
    private TransOrderSync transOrderSync;

    @Autowired
    private TransTarde transTarde;

    //线程池
    private final ExecutorService executor = Executors.newFixedThreadPool(4,
            new NamedThreadFactory("Sync",true));

    /**
     * 处理结算信息
     * @param eventId
     */
    public void handleEventSettle(Integer eventId){
        executor.execute(()->{
            TransSettle settle = new TransSettle();
            try{
                TransEvent event = transEventMapper.selectByPrimaryKey(eventId);
                if(!TransConst.EventState.end.equals(event.getState())){
                    logger.error("该事件未结束,请检查代码逻辑 eventId:{}",eventId);
                    return;
                }
                //查询事件关联订单
                TransOrder param = new TransOrder();
                param.setEventId(eventId);
                List<TransOrder> transOrders = transOrderMapper.selectAll(param);

                settle.setEventId(eventId);

                //总花费
                BigDecimal total = new BigDecimal(0);
                //总获得
                BigDecimal receive = new BigDecimal(0);
                //结算阶段
                Map<Integer,TransOrder> settleState  = new HashMap();
                //交易阶段
                int stage;
                //山寨币价格
                BigDecimal shan = BigDecimal.ZERO;
                //通用币价格价格
                BigDecimal btc = BigDecimal.ZERO;

                for(TransOrder transOrder : transOrders){
                    String co = TransCommon.getCostBi(transOrder.getHuoSymbol(),transOrder.getHuoType());
                    SymbolEnum se = SymbolEnum.getSymbolEnum(co);
                    if(se == SymbolEnum.usdt){
                        //买山寨币
                        stage = 1;
                        shan = new BigDecimal(transOrder.getHuoPrice());
                    }else if(se.getValue() == 1){
                        //山寨币卖成 btc eth
                        stage = 2;
                    }else{
                        //卖出btc
                        stage = 3;
                        btc = new BigDecimal(transOrder.getHuoPrice());
                    }
                    settleState.put(stage,transOrder);
                }
                /**
                 * todo spread3 计算收益算法 全部转换为 u计算
                 * 如果是 OrderType.SELL_LIMIT 成交量是得到的
                 * 如果是 OrderType.BUY_LIMIT 成交量是花的
                 */
                if(settleState.get(1)!=null){
                    TransOrder o1 = settleState.get(1);
                    //第一阶段 购买山寨币花费u
                    if(OrderState.FILLED.name().equals(o1.getHuoState())||OrderState.PARTIALCANCELED.name().equals(o1.getHuoState())){
                        total = total.add(new BigDecimal(o1.getHuoFilledCashAmount()));
                        receive = receive.add(new BigDecimal(o1.getHuoFilledCashAmount()).multiply(TransConst.fee));
                    }
                }
                if(settleState.get(2)!=null){
                    TransOrder o2 = settleState.get(2);
                    //第二阶段 山寨币卖成 btc eth
                    if(OrderState.FILLED.name().equals(o2.getHuoState())||OrderState.PARTIALCANCELED.name().equals(o2.getHuoState())){
                        String shanName = TransCommon.getCostBi(o2.getHuoSymbol(),o2.getHuoType());
                        //如果两个价格中有一个获取不到价格的情况下取当前交易所的收盘价格
                        if(shan.compareTo(BigDecimal.ZERO)==0 || btc.compareTo(BigDecimal.ZERO)== 0){
                            List<TransTarde.Ticker> tickers = transTarde.getTickers();
                            for(TransTarde.Ticker ticker:tickers){
                                if(ticker.symbol.equals(shanName+SymbolEnum.usdt.name())){
                                    shan = ticker.statistics.getClose();
                                }
                                if(ticker.symbol.equals(o2.getHuoSymbol().replaceAll(shanName,"")+SymbolEnum.usdt.name())){
                                    btc = ticker.statistics.getClose();
                                }

                            }
                        }

                        BigDecimal costAmount = new BigDecimal(o2.getHuoFilledAmount());
                        total = total.add(costAmount.multiply(shan));
                        BigDecimal receiveAmount = new BigDecimal(o2.getHuoFilledCashAmount()).subtract(new BigDecimal(o2.getHuoFilledFees()));
                        receive = receive.add(receiveAmount.multiply(btc));
                    }
                }
                if(settleState.get(3)!=null){
                    TransOrder o3 = settleState.get(3);
                    //第三阶段 卖出btc
                    if(OrderState.FILLED.name().equals(o3.getHuoState())||OrderState.PARTIALCANCELED.name().equals(o3.getHuoState())){
                        total = total.add(new BigDecimal(o3.getHuoFilledCashAmount()));
                        receive = receive.add(new BigDecimal(o3.getHuoFilledCashAmount()).multiply(TransConst.fee));
                    }
                }
                settle.setSettleState((settleState.get(1) == null ?"":settleState.get(1).getHuoState())+"|"
                        + (settleState.get(2) == null ?"":settleState.get(2).getHuoState())+"|"
                        + (settleState.get(3) == null ?"":settleState.get(3).getHuoState()));
                settle.setSettleTotal(total+"");
                BigDecimal earn = receive.subtract(total);
                settle.setSettleEarn(earn+"");
                BigDecimal percent = total.compareTo(BigDecimal.ZERO)==0?BigDecimal.ZERO:earn.divide(total,8,ROUND_HALF_DOWN);
                settle.setSettlePercent(percent + "");
            }catch (Exception e){
                settle.setSettleState(e.getMessage());
                logger.error("处理结算信息时发生异常 eventId:{},{}",eventId,e);
            }
            transSettleMapper.insert(settle);

        });
    }

    public static void main(String[] args) {
        Map m = new HashMap();
        m.put(1,"333");
        System.out.println(m.get(2)==null ?"11":m.get(2));
    }

    /**
     * 储存订单异步任务
     * @param pre
     */
    public void saveEndOrderSync(TransPrepareOrder pre){
        executor.execute(()->{
            try {
                TransOrder param = new TransOrder();
                param.setPrepareOrderId(pre.getPrepareOrderId());
                List<TransOrder> transOrders = transOrderMapper.selectAll(param);
                if(transOrders.size()>0){
                    logger.error("order已存在,请检查代码逻辑 prepareOrderId:{}",pre.getPrepareOrderId());
                    return;
                }

                Order huo = transOrderSync.getOrder(pre.getSymbol(),Long.parseLong(pre.getHuoOrderId()));
                TransOrder to = new TransOrder();
                //存储与预订单关联内容
                to.setEventId(pre.getEventId());
                to.setPrepareOrderId(pre.getPrepareOrderId());
                to.setCreateTime(new Date());

                //存储火币订单内容
                to.setHuoOrderId(huo.getOrderId()+"");
                to.setHuoAmount(huo.getAmount().toString());
                to.setHuoPrice(huo.getPrice().toString());
                to.setHuoSymbol(huo.getSymbol());
                to.setHuoType(huo.getType().name());
                to.setHuoState(huo.getState().name());
                to.setHuoCanceledTime(TransCommon.getDateByTimeStamp(huo.getCanceledTimestamp()));
                to.setHuoCreatedTime(TransCommon.getDateByTimeStamp(huo.getCreatedTimestamp()));
                to.setHuoFinishedTime(TransCommon.getDateByTimeStamp(huo.getFinishedTimestamp()));
                to.setHuoFilledAmount(huo.getFilledAmount()+"");
                to.setHuoFilledCashAmount(huo.getFilledCashAmount()+"");
                to.setHuoFilledFees(huo.getFilledFees()+"");
                transOrderMapper.insert(to);
                logger.info("订单已入库");
            }catch (Exception e){
                logger.error("存储火币订单时发生异常:{}",e);
            }

        });
    }
}
