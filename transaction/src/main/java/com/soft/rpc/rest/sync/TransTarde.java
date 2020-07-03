package com.soft.rpc.rest.sync;

import com.huobi.client.SyncRequestClient;
import com.huobi.client.model.DepthEntry;
import com.huobi.client.model.PriceDepth;
import com.huobi.client.model.Trade;
import com.huobi.client.model.TradeStatistics;
import com.soft.support.SymbolEnum;
import com.soft.support.TransConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 查询市场交易行情
 */
@Component
public class TransTarde {
    protected static final Logger logger = LoggerFactory.getLogger(TransTarde.class);
    SyncRequestClient syncRequestClient = SyncRequestClient.create();
    @Autowired
    private TransDepthSync transDepthSync;

    public static class Ticker
    {
        public String symbol;
        public TradeStatistics statistics;
    }


    public List<Ticker> getTickers(){
        Map<String, TradeStatistics> tickers1 = syncRequestClient.getTickers();
        List<Ticker> list = new LinkedList<>();
        for (Map.Entry<String, TradeStatistics> entry : tickers1.entrySet()) {
            Ticker ticker = new Ticker();
            ticker.symbol = entry.getKey();
            ticker.statistics = entry.getValue();
            list.add(ticker);
        }
        return list;
    }

    public BigDecimal getLastTrade(String symbol){
        Trade t =syncRequestClient.getLastTrade(symbol);
       /* logger.info("tradeId:"+t.getTradeId());
        logger.info("amount:"+t.getAmount());
        logger.info("price"+t.getPrice());
        logger.info("direction:"+t.getDirection());*/
        return t.getPrice();
    }

    /**
     * Spread3策略获取最近3次的交易信息
     * @param symbol
     * @param symbolBTC
     * @return
     * @throws Exception
     */
    public Map<String,BigDecimal> getLastTrade3(String symbol,String symbolBTC) throws Exception{
        List<Ticker> tickers = this.getTickers();
        String paibtcSym = symbol+ symbolBTC;
        BigDecimal paibtc = null;
       /* PriceDepth pb = transDepthSync.getDepth(paibtcSym);
        DepthEntry entry = pb.getBids().get(0);
        //当前最高收购价 //暂时不使用深度
        paibtc = entry.getPrice();*/

        BigDecimal paiusdt = this.getLastTrade(symbol + SymbolEnum.usdt.name());
        Thread.sleep(50);
        //中间的价格采用low
        paibtc = paibtc == null?this.getLastTrade(paibtcSym):paibtc;
        Thread.sleep(50);
        BigDecimal btcusdt = this.getLastTrade(symbolBTC + SymbolEnum.usdt.name());
        Thread.sleep(50);
        Map map = new HashMap();
        map.put(TransConst.TradeType.su,paiusdt);
        map.put(TransConst.TradeType.sb,paibtc);
        map.put(TransConst.TradeType.bu ,btcusdt);
        logger.info(symbol +"usdt:"+paiusdt+"||"+ symbol+ symbolBTC +paibtc+"||"+ symbolBTC + "usdt"+btcusdt);
        return map;
    }





}
