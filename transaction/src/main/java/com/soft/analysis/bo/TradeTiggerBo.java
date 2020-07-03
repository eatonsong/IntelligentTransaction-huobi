package com.soft.analysis.bo;

import com.huobi.client.model.request.NewOrderRequest;
import com.soft.mongo.CandlestickModel;
import com.soft.support.TransCommon;
import com.soft.support.TransException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 交易触发条件
 */
public class TradeTiggerBo {

    /**
     * 预设参数设置
     */
    //大盘上涨点数
    private  double up;
    //大盘下跌点数 负数
    private double down;
    //大盘上涨点数
    private  double upOrder;
    //大盘下跌点数 负数
    private double downOrder;
    //连续涨盘次数
    private int upflag;
    //连续跌盘次数
    private int downflag;
    //购买条件
    private Stack<String> buyCondition;
    //卖出条件
    private Stack<String> sellCondition;



    /**
     * 当前行情参数
     */
    private CandlestickModel candlestickModel;


    /**
     * 分析参数
     */
    //当前价格
    private BigDecimal now = BigDecimal.ZERO;

    //上次价格
    private BigDecimal last = BigDecimal.ZERO;

    //当日最高
    private BigDecimal todayHigh = BigDecimal.ZERO;
    //当日最低
    private BigDecimal todayLow = BigDecimal.ZERO;

    //连续涨势
    private int priceUpFlag = 0;
    //连续跌势
    private int priceDownFlag = 0;





    /**
     * 判断涨幅是否超过预设上涨点数 6个点精度
     * @return
     */
    public boolean getIsOverUp(){
        if(last == BigDecimal.ZERO || now == BigDecimal.ZERO){
            throw new TransException("当前价格或上一次价格为0");
        }
        return (now.subtract(last)).divide(last,6,BigDecimal.ROUND_DOWN).compareTo(BigDecimal.valueOf(up))>0;
    }

    /**
     * 判断涨幅是否超过预设下跌点数 6个点精度
     * @return
     */
    public boolean getIsOverDown(){
        if(last == BigDecimal.ZERO || now == BigDecimal.ZERO){
            throw new TransException("当前价格或上一次价格为0");
        }
        return (now.subtract(last)).divide(last,6,BigDecimal.ROUND_DOWN).compareTo(BigDecimal.valueOf(down))<0;
    }

    /**
     * 判断订单涨幅是否超过预设上涨点数 6个点精度
     * @return
     */
    public boolean getIsOverUpPrice(BigDecimal price){
        if(last == BigDecimal.ZERO || now == BigDecimal.ZERO){
            throw new TransException("当前价格或上一次价格为0");
        }
        return (now.subtract(price)).divide(price,6,BigDecimal.ROUND_DOWN).compareTo(BigDecimal.valueOf(upOrder))>0;
    }

    /**
     * 判断订单涨幅是否超过预设下跌点数 6个点精度
     * @return
     */
    public boolean getIsOverDownPrice(BigDecimal price){
        if(last == BigDecimal.ZERO || now == BigDecimal.ZERO){
            throw new TransException("当前价格或上一次价格为0");
        }
        return (now.subtract(price)).divide(price,6,BigDecimal.ROUND_DOWN).compareTo(BigDecimal.valueOf(downOrder))<0;
    }

    /**
     * 判断是否超过预设连涨
     * @return
     */
    public boolean getIsOverUpCount(){
        return priceUpFlag>upflag;
    }

    /**
     * 判断是否超过预设连跌
     * @return
     */
    public boolean getIsOverDownCount(){
        return priceDownFlag>downflag;
    }

    /**
     * 判断是否超过今日最高点数
     * @return
     */
    public boolean getIsOverTodayHigh(){
        if(todayHigh == BigDecimal.ZERO || now == BigDecimal.ZERO){
            throw new TransException("当前价格或今日最高为0");
        }
        return now.compareTo(todayHigh)>0;
    }

    /**
     * 判断是否跌破今日最低点数
     * @return
     */
    public boolean getIsOverTodayLow(){
        if(todayLow == BigDecimal.ZERO || now == BigDecimal.ZERO){
            throw new TransException("当前价格或今日最低为0");
        }
        return now.compareTo(todayLow)<0;
    }

    //=====================Constractor=================

    public TradeTiggerBo(double up, double down, double upOrder, double downOrder, int upflag, int downflag, Stack<String> buyCondition, Stack<String> sellCondition) {
        this.up = up;
        this.down = down;
        this.upOrder = upOrder;
        this.downOrder = downOrder;
        this.upflag = upflag;
        this.downflag = downflag;
        this.buyCondition = buyCondition;
        this.sellCondition = sellCondition;
    }


    //=======================get/set=========================

    /**
     * 行情走势
     * @param model
     * @return
     */
    public boolean setCandlestickModel(CandlestickModel model) {
        //程序开始时...初始化...
        boolean result = false;
        if(now.compareTo(BigDecimal.ZERO) == 0){
            now = model.getClose();
            todayHigh =  model.getClose();
            todayLow = model.getClose();
        }else{
            last = now;
            now = model.getClose();
            //设置当日最高和当日最低
            if(TransCommon.getDayByByTimeStamp(model.getTimestamp()).equals(TransCommon.getDayByByTimeStamp(candlestickModel.getTimestamp()))){
                if(todayHigh.compareTo(model.getClose())>0){
                    todayHigh = model.getClose();
                }
                if(todayLow.compareTo(model.getClose())<0){
                    todayLow = model.getClose();
                }
            }else{
                todayHigh =  model.getClose();
                todayLow = model.getClose();
            }

            //设置连续涨跌值
            if((now.subtract(last)).divide(last,4,BigDecimal.ROUND_DOWN).compareTo(BigDecimal.valueOf(0))>0){
                priceDownFlag = 0;
                priceUpFlag++;
            }else{
                priceDownFlag ++;
                priceUpFlag = 0;
            }
            result =  true;
        }
        this.candlestickModel = model;
        return result;
    }
    //条件组合


    /**
     * 根据方法名获取方法返回值
     * @param methodName
     * @param param
     * @return
     */
    public boolean doMethodByName(String methodName,Object param){
        try {
            Method method;
            boolean result = false;
            if(param == null){
                method = this.getClass().getMethod(methodName);
                result = (boolean)method.invoke(this);
            }else{
                method = this.getClass().getMethod(methodName, param.getClass());
                result = (boolean)method.invoke(this,param);
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("js");

    /**
     * 触发购买的条件
     * @return
     * @throws Exception
     */
    public boolean getIsBuy() throws Exception{
        String str = "";
        for(String s:this.buyCondition){
            if(!"&&".equals(s) && !"||".equals(s)){
                boolean b = doMethodByName(s,null);
                str = str+b;
            }else{
                str = str+s;
            }
        }
        if(!"".equals(str)){
            return (boolean)engine.eval(str);
        }else{
            throw new TransException("表达式为空");
        }
    }

    /**
     * 触发卖出的条件
     * @param price
     * @return
     * @throws Exception
     */
    public boolean isSale(BigDecimal price) throws Exception{
        String str = "";
        for(String s:this.sellCondition){
            if(!"&&".equals(s) && !"||".equals(s)){
                boolean b;
                if("getIsOverUpPrice".equals(s)||"getIsOverDownPrice".equals(s)){
                   b = doMethodByName(s,price);
                }else{
                    b = doMethodByName(s,null);
                }
                str = str+b;
            }else{
                str = str+s;
            }
        }
        if(!"".equals(str)){
            return (boolean)engine.eval(str);
        }else{
            throw new TransException("表达式为空");
        }
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    //===========================================get

    public double getUp() {
        return up;
    }

    public double getDown() {
        return down;
    }

    public double getUpOrder() {
        return upOrder;
    }

    public double getDownOrder() {
        return downOrder;
    }

    public int getUpflag() {
        return upflag;
    }

    public int getDownflag() {
        return downflag;
    }

    public Stack<String> getBuyCondition() {
        return buyCondition;
    }

    public Stack<String> getSellCondition() {
        return sellCondition;
    }
}
