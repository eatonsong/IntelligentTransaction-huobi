package com.soft.analysis;

import com.alibaba.fastjson.JSON;
import com.huobi.client.model.DepthEntry;
import com.huobi.client.model.PriceDepth;
import com.soft.mapper.TransAnalysisMapper;
import com.soft.po.TransAnalysis;
import com.soft.rpc.rest.sync.TransDepthSync;
import com.soft.rpc.rest.sync.TransTarde;
import com.soft.strategy.TransStrategy;
import com.soft.strategy.bo.Spread3BO;
import com.soft.support.SymbolEnum;
import com.soft.support.TransConst;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 策略分析器
 */
@Component
public class TransStrategyAnalysis extends TransStrategy {

    @Autowired
    private TransTarde transTarde;

    @Autowired
    private TransDepthSync transDepthSync;

    @Autowired
    private TransAnalysisMapper transAnalysisMapper;

    protected static final Logger logger = LoggerFactory.getLogger(TransStrategyAnalysis.class);
    //SELECT COUNT(symbol) `rank`,symbol from trans_analysis GROUP BY symbol ORDER BY  COUNT(symbol) DESC

    /**
     * 分析全币种Thread3
     */
    public void start(){
        List<TransTarde.Ticker> tickers = transTarde.getTickers();
        BigDecimal btcPrice = null;
        BigDecimal ethPrice = null;
        Iterator<TransTarde.Ticker> iterator = tickers.iterator();
        while (iterator.hasNext()){
            TransTarde.Ticker t = iterator.next();
            String symbol = t.symbol;
            if(symbol.equals(SymbolEnum.btc.name()+ SymbolEnum.usdt.name())){
                btcPrice = t.statistics.getClose();
                iterator.remove();
            }
            if(symbol.equals(SymbolEnum.eth.name()+ SymbolEnum.usdt.name())){
                ethPrice = t.statistics.getClose();
                iterator.remove();
            }
        }
        Spread3BO bo = new Spread3BO();
        bo.setType(1);
        bo.setUsdt(new BigDecimal(10));
        //this.spread3();
        //取山寨币/usdt 山寨币/btc
        BigDecimal finalBtcPrice = btcPrice;
        BigDecimal finalEthPrice = ethPrice;
        //System.out.println(tickers.size());
        List<TransTarde.Ticker> lusdt = tickers.stream().filter(t->
                t.symbol.substring(t.symbol.length()-4).equals(SymbolEnum.usdt.name())).collect(Collectors.toList());
        //删除带有usdt的
        tickers.removeAll(lusdt);

        for(TransTarde.Ticker tu:lusdt){
            String sname = tu.symbol.replaceAll(SymbolEnum.usdt.name(),"");
            bo.setsName(sname);
            bo.setSu(tu.statistics.getClose());
            for(TransTarde.Ticker fs:tickers) {
                try{
                    if (fs.symbol.equals(sname + SymbolEnum.btc.name())) {
                        bo.setbName(SymbolEnum.btc.name());
                        //将sb价设置为价格深度中的最高购买价格
                        PriceDepth pb = transDepthSync.getDepth(sname + SymbolEnum.btc.name());
                        DepthEntry entry = pb.getBids().get(0);
                        bo.setSb(entry.getPrice());

                        bo.setBu(finalBtcPrice);
                        Map<String, BigDecimal> rs = this.spread3(bo);
                        BigDecimal percent = rs.get("percent");
                        if(percent.compareTo(BigDecimal.valueOf(0.2)) == 1){
                            String jsonStr = JSON.toJSONString(bo);
                            saveAnalySis(TransConst.Strategy.spread3,bo.getsName()+bo.getbName(),jsonStr,percent.toString());
                        }
                        //logger.info("bo:{},percent:{}", bo, percent);
                    }
                    if (fs.symbol.equals(sname + SymbolEnum.eth.name())) {
                        bo.setbName(SymbolEnum.eth.name());
                        bo.setSb(fs.statistics.getClose());
                        bo.setBu(finalEthPrice);
                        Map<String, BigDecimal> rs = this.spread3(bo);
                        BigDecimal percent = rs.get("percent");
                        if(percent.compareTo(BigDecimal.valueOf(0.2)) == 1){
                            String jsonStr = JSON.toJSONString(bo);
                            saveAnalySis(TransConst.Strategy.spread3,bo.getsName()+bo.getbName(),jsonStr,percent.toString());
                        }
                        //logger.info("bo:{},percent:{}", bo, percent);
                    }
                }catch (Exception e){
                    logger.error("扫描全币种时发生异常参数:{},异常:{}",fs,e);
                }

            }
        }
    }

    public void saveAnalySis(String method,String symbol,String price,String percent){
        TransAnalysis transAnalysis = new TransAnalysis();
        transAnalysis.setCreateTime(new Date());
        transAnalysis.setMethod(method);
        transAnalysis.setSymbol(symbol);
        transAnalysis.setPrice(price);
        transAnalysis.setPercent(percent);
        try{
            PriceDepth priceDepth = transDepthSync.getDepth(symbol);
            String jsonStr = JSON.toJSONString(priceDepth);
            transAnalysis.setDepth(jsonStr);
        }catch (Exception e){
            transAnalysis.setDepth("symbol：" + symbol);
        }

        transAnalysisMapper.insert(transAnalysis);
    }


}
