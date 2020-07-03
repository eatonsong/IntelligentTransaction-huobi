package com.soft.trade;

import com.huobi.client.model.enums.OrderType;
import com.soft.mapper.TransEventMapper;
import com.soft.mapper.TransPrepareOrderMapper;
import com.soft.po.TransEvent;
import com.soft.po.TransPrepareOrder;
import com.soft.rpc.rest.sync.TransAccountSync;
import com.soft.rpc.rest.sync.TransTarde;
import com.soft.strategy.bo.Spread3BO;
import com.soft.support.SymbolEnum;
import com.soft.support.TransCommon;
import com.soft.support.TransConst;
import com.soft.strategy.TransStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

/**
 * 算法1启动类
 */
@Component
public class TradeTask {
    protected static final Logger logger = LoggerFactory.getLogger(TradeTask.class);


    @Autowired
    private TransTarde transTarde;
    @Resource
    private TransEventMapper eventMapper;
    @Resource
    private TransPrepareOrderMapper prepareOrderMapper;
    @Autowired
    private TransAccountSync transAccountSync;


    public void start(String shan,String btc,int amount) throws Exception{
        //先购买山寨币
        int type = 1;
        BigDecimal u = BigDecimal.valueOf(amount);


        Map<String,BigDecimal> map = transTarde.getLastTrade3(shan,btc);
        BigDecimal bu = map.get(TransConst.TradeType.bu);
        BigDecimal sb =map.get(TransConst.TradeType.sb);
        BigDecimal su = map.get(TransConst.TradeType.su);

        Spread3BO spread3BO = new Spread3BO();
        spread3BO.setType(type).setbName(btc).setsName(shan).setBu(bu).setSb(sb).setSu(su).setUsdt(u);
        Map<String,BigDecimal> trans = TransStrategy.spread3(spread3BO);
        BigDecimal buAmount = trans.get(TransConst.TradeType.bu);
        BigDecimal sbAmount =trans.get(TransConst.TradeType.sb);
        BigDecimal suAmount = trans.get(TransConst.TradeType.su);
        BigDecimal percent = trans.get(TransConst.TradeType.percent);

        logger.info("percent:{}",percent);

        if(percent.compareTo(TransConst.Strategy.percent) == 1){
            logger.info("percent>0触发购买操作");
            //触发购买流程
            //TODO type为0和1时区别
            //幂等性 1分钟内是否有订单未完成 卡购买山寨币
            TransPrepareOrder p = new TransPrepareOrder();
            p.setSymbol(shan + SymbolEnum.usdt.name());
            p.setState(TransConst.PrepareOrderState.doing);
            List<TransPrepareOrder> prepareOrders =  prepareOrderMapper.selectAll(p);
            for(TransPrepareOrder prepareOrder:prepareOrders){
                long min = TransCommon.getDistanceTimes(prepareOrder.getCreateTime());
                if(min<=1){
                    logger.info("1分钟内有未完成的预订单，因此不创建新事件");
                    return;
                }
            }

            //检验是否有钱 若没钱则不创建事件 全部账户余额
            //如果余额足够则创建事件
            boolean isRemain1 = transAccountSync.isRemainFull(shan+SymbolEnum.usdt.name(),OrderType.BUY_LIMIT.name(),suAmount.toString(),su.toString());
            boolean isRemain2 = transAccountSync.isRemainFull(shan+btc,OrderType.SELL_LIMIT.name(),sbAmount.toString(),sb.toString());
            boolean isRemain3 = transAccountSync.isRemainFull(btc+ SymbolEnum.usdt.name(),OrderType.SELL_LIMIT.name(),buAmount.toString(),bu.toString());

            if(isRemain1||isRemain2||isRemain3){
                logger.info("存量充足创建订单");
            }else {
                logger.info("存量不足创建订单");
                return;
            }

            //插入event
            //todo event+符号
            TransEvent e = new TransEvent();
            e.setPercent(percent.toString());
            e.setEarn(u.multiply(percent).divide(BigDecimal.valueOf(100),6,ROUND_HALF_DOWN).toString());
            e.setMethod(TransConst.Strategy.spread3);
            e.setState(TransConst.EventState.init);
            e.setTotal(u.toString());
            eventMapper.insert(e);
            //当山寨币存量不足时才创建山寨币购买订单
            long prepareOrderId1 = 0L;
            //不是存在山寨币量 应为isRemain2
            if(!isRemain2){
                prepareOrderId1 = createOrder(e.getEventId(),shan+SymbolEnum.usdt.name(),
                        su.toString(),suAmount.toString(),OrderType.BUY_LIMIT.name());
            }
            long prepareOrderId2 = createOrder(e.getEventId(),shan+btc,
                    sb.toString(),sbAmount.toString(),OrderType.SELL_LIMIT.name());
            long prepareOrderId3 = createOrder(e.getEventId(),btc+ SymbolEnum.usdt.name(),
                    bu.toString(),buAmount.toString(),OrderType.SELL_LIMIT.name());

            logger.info("============================= prepareOrderId1：{},prepareOrderId2：{}，prepareOrderId3：{}",prepareOrderId1,prepareOrderId2,prepareOrderId3);

            //开启事件
            e.setState(TransConst.EventState.doing);
            eventMapper.updateByPrimaryKey(e);

        }

    }

    /**
     * 创建订单
     * @param eventId
     * @param symbol
     * @param price
     * @param amount
     * @param type
     * @return
     */
    private int createOrder(Integer eventId,String symbol,String price,String amount,String type){
        //插入预订单
        TransPrepareOrder p = new TransPrepareOrder();
        p.setEventId(eventId);
        p.setCreateTime(new Date());
        p.setState(TransConst.PrepareOrderState.init);

        //订单1
        p.setSymbol(symbol);
        p.setPrice(price);
        p.setAmount(amount);
        p.setType(type);
        prepareOrderMapper.insert(p);
        int prepareOrderId = p.getPrepareOrderId();
        return prepareOrderId;
    }



}
