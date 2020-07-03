package com.soft;

import com.huobi.client.model.Order;
import com.soft.analysis.TransStrategyAnalysis;
import com.soft.contract.ConstractTrade;
import com.soft.po.TransOrder;
import com.soft.po.TransPrepareOrder;
import com.soft.support.NamedThreadFactory;
import com.soft.support.SymbolEnum;
import com.soft.trade.ScanTask;
import com.soft.trade.TradeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * 调度器
 */
@Component
public class Scheduler {
    protected static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private long initialDelay = 15L;

    //定时任务执行器
    private ScheduledExecutorService scheduledScan = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("Scan",true));
    private ScheduledExecutorService scheduledTrade = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("Trade",true));
    private ScheduledExecutorService scheduledAnalysis = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("Analysis",true));

    @Autowired
    private ScanTask scanTask;
    @Autowired
    private TradeTask tradeTask;
    @Autowired
    private ConstractTrade constractTrade;
    @Autowired
    private TransStrategyAnalysis transStrategyAnalysis;

/*    @Autowired
    private TransOrderMapper transOrderMapper;*/


    @PostConstruct
    public void init(){
        //交易线程
        ScheduledFuture<?> sendFuture =  scheduledScan.scheduleWithFixedDelay(()->{
            try {
                //scanTask.start();
                //constractTrade.startOpen();
            }catch (Exception e){
                logger.error("ScanTask 出现异常:{}",e);
            }
        },initialDelay,120L, TimeUnit.SECONDS);

        //扫描线程
        ScheduledFuture<?> sendFuture1 =  scheduledTrade.scheduleWithFixedDelay(()->{
            try {
//                tradeTask.start(SymbolEnum.dta.name(),SymbolEnum.eth.name(),10);
//                tradeTask.start(SymbolEnum.pai.name(),SymbolEnum.eth.name(),10);
//                tradeTask.start(SymbolEnum.dta.name(),SymbolEnum.btc.name(),10);
//                tradeTask.start(SymbolEnum.pai.name(),SymbolEnum.btc.name(),10);
                //constractTrade.startClose();

            }catch (Exception e){
                logger.error("TradeTask 出现异常:{}",e);
            }
        },initialDelay,300L, TimeUnit.SECONDS);


        //统计线程
//        scheduledScan.scheduleWithFixedDelay(()->{
//            try {
//                //transStrategyAnalysis.start();
//            }catch (Exception e){
//                logger.error("ScanTask 出现异常:{}",e);
//            }
//        },initialDelay,60L, TimeUnit.SECONDS);
    }








}
