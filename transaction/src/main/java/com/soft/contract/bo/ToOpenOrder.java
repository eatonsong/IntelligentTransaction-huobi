package com.soft.contract.bo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 待开单
 */
public class ToOpenOrder {

    private BigDecimal percent;
    private String direction;//	true	string	"buy":买 "sell":卖
    private String offset;//	true	string	"open":开 "close":平
    private int lever_rate;//	true	int	杠杆倍数	1\5\10\20 [“开仓”若有10倍多单，就不能再下20倍多单]

    public BigDecimal getPercent() {
        return percent;
    }

    public ToOpenOrder setPercent(BigDecimal percent) {
        this.percent = percent;
        return this;
    }

    public String getDirection() {
        return direction;
    }

    public ToOpenOrder setDirection(String direction) {
        this.direction = direction;
        return this;
    }

    public String getOffset() {
        return offset;
    }

    public ToOpenOrder setOffset(String offset) {
        this.offset = offset;
        return this;
    }

    public int getLever_rate() {
        return lever_rate;
    }

    public ToOpenOrder setLever_rate(int lever_rate) {
        this.lever_rate = lever_rate;
        return this;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToOpenOrder that = (ToOpenOrder) o;
        return lever_rate == that.lever_rate &&
                Objects.equals(percent, that.percent) &&
                Objects.equals(direction, that.direction) &&
                Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(percent, direction, offset, lever_rate);
    }
}
