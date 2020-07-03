package com.soft.contract.bo;

import java.math.BigDecimal;

public class PositionInfo {

    private String symbol;//	true	string	品种代码	"BTC","ETH"...
    private String contract_code;//	true	string	合约代码	"BTC180914" ...
    private String contract_type;//	true	string	合约类型	当周:"this_week", 次周:"next_week", 季度:"quarter"
    private BigDecimal volume;//	true	decimal	持仓量
    private BigDecimal available;//	true	decimal	可平仓数量
    private BigDecimal frozen;//	true	decimal	冻结数量
    private BigDecimal cost_open;//	true	decimal	开仓均价
    private BigDecimal cost_hold;//	true	decimal	持仓均价
    private BigDecimal profit_unreal;//	true	decimal	未实现盈亏
    private BigDecimal  profit_rate;//	true	decimal	收益率
    private BigDecimal profit;//	true	decimal	收益
    private BigDecimal position_margin;//	true	decimal	持仓保证金
    private BigDecimal lever_rate;//	true	int	杠杠倍数
    private String direction;//	true	string	"buy":买 "sell":卖
    private BigDecimal last_price;//	true	decimal	最新价


    public String getSymbol() {
        return symbol;
    }

    public PositionInfo setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getContract_code() {
        return contract_code;
    }

    public PositionInfo setContract_code(String contract_code) {
        this.contract_code = contract_code;
        return this;
    }

    public String getContract_type() {
        return contract_type;
    }

    public PositionInfo setContract_type(String contract_type) {
        this.contract_type = contract_type;
        return this;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public PositionInfo setVolume(BigDecimal volume) {
        this.volume = volume;
        return this;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public PositionInfo setAvailable(BigDecimal available) {
        this.available = available;
        return this;
    }

    public BigDecimal getFrozen() {
        return frozen;
    }

    public PositionInfo setFrozen(BigDecimal frozen) {
        this.frozen = frozen;
        return this;
    }

    public BigDecimal getCost_open() {
        return cost_open;
    }

    public PositionInfo setCost_open(BigDecimal cost_open) {
        this.cost_open = cost_open;
        return this;
    }

    public BigDecimal getCost_hold() {
        return cost_hold;
    }

    public PositionInfo setCost_hold(BigDecimal cost_hold) {
        this.cost_hold = cost_hold;
        return this;
    }

    public BigDecimal getProfit_unreal() {
        return profit_unreal;
    }

    public PositionInfo setProfit_unreal(BigDecimal profit_unreal) {
        this.profit_unreal = profit_unreal;
        return this;
    }

    public BigDecimal getProfit_rate() {
        return profit_rate;
    }

    public PositionInfo setProfit_rate(BigDecimal profit_rate) {
        this.profit_rate = profit_rate;
        return this;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public PositionInfo setProfit(BigDecimal profit) {
        this.profit = profit;
        return this;
    }

    public BigDecimal getPosition_margin() {
        return position_margin;
    }

    public PositionInfo setPosition_margin(BigDecimal position_margin) {
        this.position_margin = position_margin;
        return this;
    }

    public BigDecimal getLever_rate() {
        return lever_rate;
    }

    public PositionInfo setLever_rate(BigDecimal lever_rate) {
        this.lever_rate = lever_rate;
        return this;
    }

    public String getDirection() {
        return direction;
    }

    public PositionInfo setDirection(String direction) {
        this.direction = direction;
        return this;
    }

    public BigDecimal getLast_price() {
        return last_price;
    }

    public PositionInfo setLast_price(BigDecimal last_price) {
        this.last_price = last_price;
        return this;
    }
}
