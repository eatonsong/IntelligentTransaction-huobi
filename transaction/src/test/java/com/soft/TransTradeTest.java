package com.soft;

import com.huobi.client.model.enums.AccountType;
import com.huobi.client.model.enums.OrderType;
import com.huobi.client.model.request.NewOrderRequest;
import com.soft.analysis.bo.TradeResultBo;
import com.soft.analysis.bo.TradeTiggerBo;
import com.soft.mapper.TransAnalysisTradeMapper;
import com.soft.mapper.TransAnalysisTradeResultMapper;
import com.soft.mapper.TransAnalysisTradeStartMapper;
import com.soft.mongo.CandlestickModel;
import com.soft.mongo.MongodbTestModel;
import com.soft.mongo.MongodbUtils;
import com.soft.po.TransAnalysisTrade;
import com.soft.po.TransAnalysisTradeResult;
import com.soft.po.TransAnalysisTradeStart;
import com.soft.support.TransCommon;
import com.soft.trade.ScanTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransTradeTest {
    protected static final Logger logger = LoggerFactory.getLogger(TransTradeTest.class);
    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat dfH = new SimpleDateFormat("HH");

    @Autowired
    private TransAnalysisTradeMapper transAnalysisTradeMapper;
    @Autowired
    private TransAnalysisTradeResultMapper transAnalysisTradeResultMapper;
    @Autowired
    private TransAnalysisTradeStartMapper transAnalysisTradeStartMapper;
    @Test
    public void test() throws Exception{
        /**
         * 买盘条件组合
         * 大盘跌x点以上 大盘涨x点以上 跌破当日最低 连跌次数 连涨次数
         * getIsOverDown
         */
        //String buyMethod[] = {"getIsOverDown","getIsOverUp","getIsOverTodayLow","getIsOverDownCount","getIsOverUpCount"};
        String buyMethod[] = {"getIsOverDown"};
        List<Stack> buyConditionList = TransCommon.getConditions(buyMethod);
        logger.info("组装好的买盘组合数量:{}",buyConditionList.size());
        /**
         * 卖盘条件组合
         * 大盘跌x点 大盘涨x点  价格涨破最高   连跌次数 连涨次数  订单涨x点 订单跌x点
         */
        String sellMethod[] = {"getIsOverTodayHigh","getIsOverDownCount","getIsOverUpCount","getIsOverUpPrice","getIsOverDownPrice"};
        //String sellMethod[] = {"getIsOverDownCount","getIsOverUpPrice"};
        List<Stack> sellConditionList = TransCommon.getConditions(sellMethod);
        logger.info("组装好的卖盘组合数量:{}",sellConditionList.size());

        List<TradeTiggerBo> ll = new ArrayList<>();
        logger.info("开始组装测试类...");
        for(Stack buy:buyConditionList){
            for(Stack sell:sellConditionList){
                ll.add(new TradeTiggerBo(0.01,-0.01,0.01,-0.01,2,2,buy,sell));
            }
        }
        logger.info("组装好的测试类数量:{}",ll.size());

        logger.info("开始测试.....");
        long count = 0;
        for(TradeTiggerBo bo:ll){
            count++;
            System.out.println("当前测试次数:"+count);
            startTransTradeTest(bo);
        }
        logger.info("结束测试.....");
    }


    //@Test
    public void startTransTradeTest(TradeTiggerBo strat) throws Exception{
        String symbol = "eosusdt";
        BigDecimal initMoney = BigDecimal.valueOf(1000);
        //
        TradeResultBo result = new TradeResultBo(initMoney);
        //存储订单
        List<NewOrderRequest> orders = new ArrayList<>();
        //结算时价格
        BigDecimal lastPrice = BigDecimal.ZERO;


        //todo 1.mongo中取出k线 循环模拟盘走势
        List<? extends Object> findAll = MongodbUtils.findAll(new CandlestickModel(),symbol);
        for(Object m :findAll){
            CandlestickModel model = (CandlestickModel)m;
            lastPrice = model.getClose();
            boolean init = strat.setCandlestickModel(model);
            if (!init){continue;}
            //夜间时间不操作，记录最高最低价
            String hour = TransCommon.getHourByByTimeStamp(model.getTimestamp());
            if("00".equals(hour)||"01".equals(hour)||"02".equals(hour)||"03".equals(hour)||"04".equals(hour)){
                continue;
            }

            //触发购买的条件
            boolean isBuy = strat.getIsBuy();
            if(isBuy){
                //创建订单 根据当前订单中买盘的金额来取 如果没有，就取1/5的钱出来
                BigDecimal amount;
                if(orders.size() == 0){
                    //将钱分成5份来用
                    amount  = result.getMoney().divide(BigDecimal.valueOf(5),4,BigDecimal.ROUND_DOWN).divide(model.getClose(),4,BigDecimal.ROUND_DOWN);
                }else {
                    amount = orders.get(0).getAmount();
                }
                if(result.getMoney().compareTo(amount.multiply(model.getClose()))>0){
                    //logger.info("开始买入price{},amount{},time{},strat:{}",model.getClose(),amount,TransCommon.getDateByTimeStamp(model.getTimestamp()),strat);
                    NewOrderRequest newOrderRequest = new NewOrderRequest("eosusdt",  AccountType.SPOT,OrderType.BUY_MARKET, amount,model.getClose());
                    orders.add(newOrderRequest);
                    BigDecimal cost = (amount.multiply(model.getClose())).multiply(BigDecimal.valueOf(1.002));
                    result.startBuy(cost);
                    //logger.info("剩余金额:{}",money);
                }
            }

            //查询订单接到了就出售
            Iterator it = orders.iterator();
            while(it.hasNext()) {
                NewOrderRequest order = (NewOrderRequest) it.next();
                //卖出条件
                boolean isSale = strat.isSale(order.getPrice());
                if(isSale){
                    //logger.info("开始卖出price{},amount{},time{},start:{}",model.getClose(),order.getAmount(),TransCommon.getDateByTimeStamp(model.getTimestamp()),strat);
                    BigDecimal add = (order.getAmount().multiply(model.getClose())).multiply(BigDecimal.valueOf(0.998));
                    result.startSell(add);
                    it.remove();
                }
            }

            /**
             * 当日清算
             */
            //晚上12点进行清盘的操作d
            String time = df.format(TransCommon.getDateByTimeStamp(model.getTimestamp()));
            if("23:59:00".equals(time)){
                BigDecimal add = BigDecimal.ZERO;
                Iterator it1 = orders.iterator();
                while(it1.hasNext()) {
                    NewOrderRequest order = (NewOrderRequest) it1.next();
                    //logger.info("开始清算卖出price{},amount{},time{}",model.getClose(),order.getAmount(),TransCommon.getDateByTimeStamp(model.getTimestamp()));
                    add = add.add((order.getAmount().multiply(model.getClose())).multiply(BigDecimal.valueOf(0.998)));
                    it1.remove();
                }
                result.startClear(add);
                //logger.info("开始清算==={}",result);
            }
        }

        //最终结算金额全部相加
        BigDecimal add1 = BigDecimal.ZERO;
        for(NewOrderRequest orderRequest:orders){
            add1 = (orderRequest.getAmount().multiply(lastPrice).add(add1));
        }

        result.startClear(add1);
        if(result.getMoney().compareTo(BigDecimal.valueOf(1000))>0){
            logger.info("测试结果利好:result{},start{}",result,strat);
        }else{
            logger.info("测试结果利空:result{},start{}",result,strat);
        }
        saveRecord(strat,result,symbol,initMoney);
        //logger.info("最终结算金额:{},总金额最高值:{},总金额最低值:{},成交总买单：{},成交总卖单:{}",money,highMoney,lowMoney,countBuyAll,countSellAll);

        //todo 2.实际操作
        //跌？个点下市价单，涨回再抛掉 （以什么价格为准，一直没涨咋办）
        //              （00点之后1个小时观望，然后以当日最高最低来算   00点清盘结算）

        //合约交易 -涨了就买跌 跌了买涨 （一直涨或者一直跌怎么办，合约风险系数高）

        //币种互换 （如何盈利的方式）
        //todo 3.统计结算结果，以图表形式展示 --需要页面

    }

    private void saveRecord(TradeTiggerBo start,TradeResultBo result,String symbol,BigDecimal initMoney){
        TransAnalysisTradeResult record = new TransAnalysisTradeResult();
        record.setCountBuyAll(result.getCountBuyAll()+"");
        record.setCountSellAll(result.getCountSellAll()+"");
        record.setHighMoney(result.getHighMoney()+"");
        record.setLowMoney(result.getLowMoney()+"");
        record.setMoney(result.getMoney()+"");
        transAnalysisTradeResultMapper.insert(record);
        TransAnalysisTradeStart tats = new TransAnalysisTradeStart();
        tats.setUp(start.getUp()+"");
        tats.setDown(start.getDown()+"");
        tats.setUpflag(start.getUpflag()+"");
        tats.setDownflag(start.getDownflag()+"");
        tats.setBuyCondition(start.getBuyCondition().toString());
        tats.setSellCondition(start.getSellCondition().toString());
        tats.setDownOrder(start.getDownOrder()+"");
        tats.setUpOrder(start.getUpOrder()+"");
        transAnalysisTradeStartMapper.insert(tats);
        TransAnalysisTrade tat = new TransAnalysisTrade();
        tat.setSymbol(symbol);
        tat.setInitMoney(initMoney+"");
        tat.setStratId(tats.getStartId());
        tat.setResultId(record.getResultId());
        transAnalysisTradeMapper.insert(tat);

    }
    //===================================


    /**
     * ========================================实验结果=============================================================
     * 按照分钟线收盘涨跌来测试
     * 1.分钟跌幅1% 无测试结果（程序存在bug）
     * 2.分钟跌幅0.5% 无测试结果（程序存在bug）
     *
     * 1.按分钟线每次连续收盘价格跌3次 + 涨1个点卖出   ============ 最终还剩369 惨淡收场
     * 2.按分钟线每次连续收盘价格跌3次 + 每晚12点进行清盘 + 涨1个点卖出 ===============  最终结算金额:0.01985337634 比上面还要惨
     * 3.尝试当天最高值最低值来购买 + 12点至早4点不购买 + 每晚11:58点进行清盘 + 动态卖出 ======== 最终结算金额:0.469 前7天赚钱，后面每天都在亏钱 最后亏到 比之前相比要强许多了啊
     * 4. 3的全部内容加上跌1个点直接卖出 ====================最终结算金额:0.0009890136 一直在亏钱
     * 5. 分钟跌1%以上并且连续2个涨盘 + 3的全部内容 =============== 没有成交的单子
     * 6.todo 单独测试跌1%以上 ============== 最终结算金额:1404.09367846524 最高涨到1810 这个看起来很可行啊哈哈
     * 7. 继续在1个点左右测试看最优点数
     *    0.80
     *    0.85
     *    0.90
     *    0.95
     *    1.05
     *    1.1
     *    1.15
     *    1.2
     *
     *
     *
     * tag 核心问题就是什么时候买入什么时候卖出
     *
     *
     *
     */


}
