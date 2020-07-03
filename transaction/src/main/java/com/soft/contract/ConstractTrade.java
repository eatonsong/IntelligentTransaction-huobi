package com.soft.contract;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.huobi.client.exception.HuobiApiException;
import com.huobi.common.api.HbdmClient;
import com.huobi.common.api.HbdmRestApiV1;
import com.huobi.common.api.IHbdmRestApi;
import com.huobi.common.request.Order;
import com.soft.contract.bo.ContractOrder;
import com.soft.contract.bo.ContractTick;
import com.soft.contract.bo.PositionInfo;
import com.soft.contract.bo.ToOpenOrder;
import com.soft.support.SymbolEnum;
import com.soft.support.TransConst;
import com.soft.support.TransException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

/**
 * 合约交易
 */
@Component
public class ConstractTrade {

    private static Logger logger = LoggerFactory.getLogger(ConstractTrade.class);
    private List<ToOpenOrder> base = null;
    /**
     * get请求无需发送身份认证,通常用于获取行情，市场深度等公共信息
     */
    String api_key = TransConst.AccessKey; // huobi申请的apiKey,API调试过程中有问题或者有疑问可反馈微信号shaoxiaofeng1118
    String secret_key = TransConst.SecretKey; // huobi申请的secretKey
    String url_prex = "https://api.hbdm.com";//火币api接口地址https://api.hbdm.com
    IHbdmRestApi futureGetV1 = new HbdmRestApiV1(url_prex);
    IHbdmRestApi futurePostV1 = new HbdmRestApiV1(url_prex, api_key, secret_key);

    @PostConstruct
    private void init(){
        base = new ArrayList<>();
        for(double s = -360;s <= 360;s = s+20){
            if(s != 0){
                ToOpenOrder toOpenOrder = new ToOpenOrder();
                if(s < 0){
                    toOpenOrder.setDirection("buy");
                }else {
                    toOpenOrder.setDirection("sell");
                }
                toOpenOrder.setPercent(BigDecimal.valueOf(s).setScale(2,ROUND_HALF_DOWN));
                base.add(toOpenOrder);
            }
        }
    }

    /**
     * 开仓
     * @throws Exception
     */
    public void startOpen() throws Exception{

        // 获取聚合行情
        String merged = futureGetV1.futureMarketDetailMerged("EOS_CW");
        ContractTick contractTick = getResponse(merged,ContractTick.class);
        logger.info("获取聚合行情：close"+contractTick.getClose());

        // 获取合约当前未成交委托
        String openorders = futurePostV1.futureContractOpenorders(SymbolEnum.eos.name(), "1", "100");
        logger.info("获取合约当前未成交委托" + openorders);
        List<ContractOrder> l = (List) getResponse(openorders,ContractOrder.class);
        //下单 open buy 20-400 sell 20-400
        List<ToOpenOrder>  too = new ArrayList<>();
        for(ContractOrder c : l){
            if("open".equals(c.getOffset())){
                BigDecimal percent = (c.getPrice().subtract(contractTick.getClose())).divide(contractTick.getClose(),2,ROUND_HALF_DOWN)
                        .multiply(TransConst.LeverRate.l20).multiply(BigDecimal.valueOf(100));
                //验证一下20-400的单子中没有哪个就下哪个
                ToOpenOrder toOpenOrder = new ToOpenOrder().setPercent(percent).setDirection(c.getDirection());
                too.add(toOpenOrder);
            }else{
                //todo 平单
            }
        }

        List<ToOpenOrder> result = getToOpen(too);
        //开始下单
        if(result.size() == 0){
            logger.info("系统检测到当前区间没有可下单");
            return;
        }
        logger.info("系统检测到当前区间内有可以下单量:{},开始下单...." + result.size());
        List<Order> orders = new ArrayList<>();
        for (ToOpenOrder r:result){
            BigDecimal pp = r.getPercent().divide(BigDecimal.valueOf(100)).divide(TransConst.LeverRate.l20).add(BigDecimal.ONE);
            BigDecimal price = contractTick.getClose().multiply(pp).setScale(3,ROUND_HALF_DOWN);
            // 合约下单
            logger.info("price:{},drict:{}",price,r.getDirection());
            String contractOrder = futurePostV1.futureContractOrder(SymbolEnum.eos.name(), "this_week",
                    "", "", price.toString(), "10",
                    r.getDirection(), "open", TransConst.LeverRate.l20.toString(), "limit");

            logger.info("合约下单返回" + contractOrder);
        }
//

//        // 获取合约信息
//        String contractInfo = futureGetV1.futureContractInfo(SymbolEnum.eos.name(), "this_week", "");
//        logger.info("获取合约信息" + contractInfo);
//        // 获取合约指数信息
//        String contractindex = futureGetV1.futureContractIndex("EOS");
//        logger.info("获取合约指数信息" + contractindex);
//
//        // 获取合约最高限价和最低限价
//        String pricelimit = futureGetV1.futurePriceLimit("EOS", "this_week", "");
//        logger.info("获取合约最高限价和最低限价" + pricelimit);

//
//        // 获取行情深度数据
//        String marketDepth = futureGetV1.futureMarketDepth("EOS", "step0");
//        logger.info("获取行情深度数据" + marketDepth);
//
//        // 获取用户账户信息
//        String accountInfo = futurePostV1.futureContractAccountInfo("EOS");
//        logger.info("获取用户账户信息" + accountInfo);
//        // 获取合约历史委托
//        String orderDetail = futurePostV1.futureContractHisorders("EOS", "0", "1", "0", "90", "1", "20");
//        logger.info("获取合约历史委托" + orderDetail);

    }

    /**
     * 平仓
     * @throws Exception
     */
    public void startClose() throws Exception{

        // 获取用户持仓信息
        String positionInfo = futurePostV1.futureContractPositionInfo("EOS");
        logger.info("获取用户持仓信息" + positionInfo);
        List<PositionInfo> positionInfos = (List) getResponse(positionInfo, PositionInfo.class);
        for(PositionInfo pi : positionInfos){
            if(pi.getAvailable().compareTo(BigDecimal.ZERO) == 1){
                //有可用于平单的量
                BigDecimal open = pi.getCost_open();
                //一半下 0.5%（10%） 的平仓 一半下1%的平仓
                BigDecimal price05 = null;
                BigDecimal price10 = null;
                if("buy".equals(pi.getDirection())){
                    //买盘
                    price05 = open.multiply(BigDecimal.valueOf(1.005)).setScale(3,ROUND_HALF_DOWN);
                    BigDecimal vail5 = pi.getAvailable().multiply(BigDecimal.valueOf(0.5)).setScale(0,ROUND_HALF_DOWN);
                    price10 = open.multiply(BigDecimal.valueOf(1.01)).setScale(3,ROUND_HALF_DOWN);
                    BigDecimal vail10 = pi.getAvailable().subtract(vail5).setScale(0,ROUND_HALF_DOWN);
                    String contractOrder = futurePostV1.futureContractOrder(SymbolEnum.eos.name(), "this_week",
                            "", "", price05.toString(),vail5.toString(),
                            "sell", "close", TransConst.LeverRate.l20.toString(), "limit");
                    logger.info("平仓合约下单返回" + contractOrder);
                    String contractOrder1 = futurePostV1.futureContractOrder(SymbolEnum.eos.name(), "this_week",
                            "", "", price10.toString(), vail10.toString(),
                            "sell", "close", TransConst.LeverRate.l20.toString(), "limit");
                    logger.info("平仓合约下单返回" + contractOrder1);
                }else{
                    price05 = open.multiply(BigDecimal.valueOf(0.995)).setScale(3,ROUND_HALF_DOWN);
                    BigDecimal vail5 = pi.getAvailable().multiply(BigDecimal.valueOf(0.5)).setScale(0,ROUND_HALF_DOWN);
                    price10 = open.multiply(BigDecimal.valueOf(0.99)).setScale(3,ROUND_HALF_DOWN);
                    BigDecimal vail10 = pi.getAvailable().subtract(vail5).setScale(0,ROUND_HALF_DOWN);
                    String contractOrder = futurePostV1.futureContractOrder(SymbolEnum.eos.name(), "this_week",
                            "", "", price05.toString(),vail5.toString(),
                            "buy", "close", TransConst.LeverRate.l20.toString(), "limit");
                    logger.info("平仓合约下单返回" + contractOrder);
                    String contractOrder1 = futurePostV1.futureContractOrder(SymbolEnum.eos.name(), "this_week",
                            "", "", price10.toString(), vail10.toString(),
                            "buy", "close", TransConst.LeverRate.l20.toString(), "limit");
                    logger.info("平仓合约下单返回" + contractOrder1);
                }
            }
        }
    }


    /**
     * 匹配出当前未下开仓
     * @param too
     * @return
     */
    public List<ToOpenOrder> getToOpen(List<ToOpenOrder> too){
        if(base == null){
            init();
        }
        List<ToOpenOrder> common = new ArrayList();
        for(ToOpenOrder ba: base){
            for(ToOpenOrder to : too){
                if(ba.getDirection().equals(to.getDirection()) && ba.getPercent().compareTo(to.getPercent()) == 0){
                    common.add(ba);
                }
            }
        }
        List<ToOpenOrder> result = Lists.newArrayList(base);
        result.removeAll(common);
        return result;
    }


    /**
     * 远程访问结果转换
     * @param str
     * @param c
     * @param <T>
     * @return
     */
    public static  <T>T getResponse(String str,Class<T> c){
        if(str == null || "".equals(str)){
            throw new TransException("获取返回结果失败 - 入参为空");
        }
        JSONObject object = JSONObject.parseObject(str);
        String status = (String) object.get("status");
        if(status == null || !"ok".equals(status)){
            throw new TransException("获取返回结果失败 - status:" + status);
        }
        if(c == ContractOrder.class){
            JSONObject data = (JSONObject) object.get("data");
            if(data == null){
                throw new TransException("获取返回结果失败 - data为空");
            }
            JSONArray orders = (JSONArray) data.get("orders");
            List os = orders.toJavaList(ContractOrder.class);
            return (T) os;
        }
        if(c == PositionInfo.class){
            JSONArray data = (JSONArray) object.get("data");
            if(data == null){
                throw new TransException("获取返回结果失败 - data为空");
            }
            List pi = data.toJavaList(PositionInfo.class);
            return (T) pi;
        }
        if(c == ContractTick.class){
            JSONObject tick = (JSONObject) object.get("tick");
            if(tick == null){
                throw new TransException("获取返回结果失败 - tick为空");
            }
            return (T)tick.toJavaObject(ContractTick.class);
        }
        return null;
    }




}
