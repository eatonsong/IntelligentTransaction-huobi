package com.soft.contract.bo;

import java.math.BigDecimal;

public class ContractTick {

    private String id;//: K线id,
    private long vol;//: 成交量（张），买卖双边成交量之和,
    private long   count;//: 成交笔数,
    private BigDecimal        open;//: 开盘价,
    private BigDecimal         close;//: 收盘价,当K线为最晚的一根时，是最新成交价
    private BigDecimal   low;//: 最低价,
    private BigDecimal    high;//: 最高价,
    private BigDecimal       amount;//: 成交量(币), 即 sum(每一笔成交量(张)*单张合约面值/该笔成交价)
    private String         bid;//: [买1价,买1量(张)],
    private String        ask;//: [卖1价,卖1量(张)]

    public String getId() {
        return id;
    }

    public ContractTick setId(String id) {
        this.id = id;
        return this;
    }

    public long getVol() {
        return vol;
    }

    public ContractTick setVol(long vol) {
        this.vol = vol;
        return this;
    }

    public long getCount() {
        return count;
    }

    public ContractTick setCount(long count) {
        this.count = count;
        return this;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public ContractTick setOpen(BigDecimal open) {
        this.open = open;
        return this;
    }

    public BigDecimal getClose() {
        return close;
    }

    public ContractTick setClose(BigDecimal close) {
        this.close = close;
        return this;
    }

    public BigDecimal getLow() {
        return low;
    }

    public ContractTick setLow(BigDecimal low) {
        this.low = low;
        return this;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public ContractTick setHigh(BigDecimal high) {
        this.high = high;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ContractTick setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public String getBid() {
        return bid;
    }

    public ContractTick setBid(String bid) {
        this.bid = bid;
        return this;
    }

    public String getAsk() {
        return ask;
    }

    public ContractTick setAsk(String ask) {
        this.ask = ask;
        return this;
    }
}