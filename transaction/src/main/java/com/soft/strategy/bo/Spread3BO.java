package com.soft.strategy.bo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 三分法策略BO
 */
public class Spread3BO {
    //type 0先购买通用币 1先购买山寨币
    int type;
    //bu 通用币价
    BigDecimal bu;
    //sb 山寨币/通用币
    BigDecimal sb;
    //su 山寨币
    BigDecimal su;
    //usdt 初始数量
    BigDecimal usdt;
    //山寨币名称
    String sName;
    //通用币名称
    String bName;

    public int getType() {
        return type;
    }

    public Spread3BO setType(int type) {
        this.type = type;
        return this;
    }

    public BigDecimal getBu() {
        return bu;
    }

    public Spread3BO setBu(BigDecimal bu) {
        this.bu = bu;
        return this;
    }

    public BigDecimal getSb() {
        return sb;
    }

    public Spread3BO setSb(BigDecimal sb) {
        this.sb = sb;
        return this;
    }

    public BigDecimal getSu() {
        return su;
    }

    public Spread3BO setSu(BigDecimal su) {
        this.su = su;
        return this;
    }

    public BigDecimal getUsdt() {
        return usdt;
    }

    public Spread3BO setUsdt(BigDecimal usdt) {
        this.usdt = usdt;
        return this;
    }

    public String getsName() {
        return sName;
    }

    public Spread3BO setsName(String sName) {
        this.sName = sName;
        return this;
    }

    public String getbName() {
        return bName;
    }

    public Spread3BO setbName(String bName) {
        this.bName = bName;
        return this;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
