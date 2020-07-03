package com.soft.analysis.bo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 交易结果
 */
public class TradeResultBo {

    /**
     * 预设参数
     */
    //初始金额
    BigDecimal money;

    /**
     * 操作参数
     */
    //当天成交买单
    int countBuy = 0;
    //当天成交卖单
    int countSell = 0;

    //成交总单数
    int countBuyAll = 0;
    int countSellAll = 0;

    //总金额最高值
    BigDecimal highMoney;
    //总金额最低值
    BigDecimal lowMoney;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    //======================操作方法==========================
    public void startBuy(BigDecimal cost){
        //下单后总金额减少
        money = money.subtract(cost);
        countBuy ++;
        countBuyAll ++;
    }

    public void startSell(BigDecimal add){
        money = money.add(add);
        countSell++;
        countSellAll ++;
    }

    public void startClear(BigDecimal add){
        money = money.add(add);
        countBuy = 0;
        countSell = 0;
        if(money.compareTo(highMoney)>0){
            highMoney = money;
        }
        if(money.compareTo(lowMoney)<0){
            lowMoney = money;
        }
    }


    //====================构造器==================

    public TradeResultBo(BigDecimal money) {
        this.money = money;
        this.highMoney = money;
        this.lowMoney = money;
    }


    //===============getset=====================


    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public int getCountBuy() {
        return countBuy;
    }

    public int getCountSell() {
        return countSell;
    }

    public int getCountBuyAll() {
        return countBuyAll;
    }

    public int getCountSellAll() {
        return countSellAll;
    }

    public BigDecimal getHighMoney() {
        return highMoney;
    }

    public BigDecimal getLowMoney() {
        return lowMoney;
    }
}
