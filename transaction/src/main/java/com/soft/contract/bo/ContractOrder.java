package com.soft.contract.bo;

import java.math.BigDecimal;

/**
 * 合约订单类
 */
public class ContractOrder {
    //下订单相关
    private String symbol;	//string	true	"BTC","ETH"...
    private String contract_type;//	string	true	合约类型 ("this_week":当周 "next_week":下周 "quarter":季度)
    private String contract_code;//	true	string	合约代码	"BTC180914" ...
    private long volume;//	long	true	委托数量(张)
    private BigDecimal price;//	true	decimal	委托价格
    private String order_price_type;//	string	true	订单报价类型 "limit":限价 "opponent":对手价 "post_only":只做maker单,post only下单只受用户持仓数量限制,optimal_5：最优5档、optimal_10：最优10档、optimal_20：最优20档
    private String direction;//	true	string	"buy":买 "sell":卖
    private String offset;//	true	string	"open":开 "close":平
    private int lever_rate;//	true	int	杠杆倍数	1\5\10\20 [“开仓”若有10倍多单，就不能再下20倍多单]
    private long client_order_id;//	true	long	客户订单ID 客户自己填写和维护，必须为数字

    //订单明细
    private long order_id;//	true	long	订单ID
    private long created_at;//	true	long	订单创建时间
    private BigDecimal trade_volume;//	true	decimal	成交数量
    private BigDecimal trade_turnover;//	true	decimal	成交总金额
    private BigDecimal fee;//	true	decimal	手续费
    private BigDecimal trade_avg_price;//	true	decimal	成交均价
    private BigDecimal margin_frozen;//	true	decimal	冻结保证金
    private BigDecimal profit;//	true	decimal	收益
    private int status;//	true	int	订单状态	(3未成交 4部分成交 5部分成交已撤单 6全部成交 7已撤单)
    private String order_source;//	true	string	订单来源


    public String getSymbol() {
        return symbol;
    }

    public ContractOrder setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getContract_type() {
        return contract_type;
    }

    public ContractOrder setContract_type(String contract_type) {
        this.contract_type = contract_type;
        return this;
    }

    public String getContract_code() {
        return contract_code;
    }

    public ContractOrder setContract_code(String contract_code) {
        this.contract_code = contract_code;
        return this;
    }

    public long getVolume() {
        return volume;
    }

    public ContractOrder setVolume(long volume) {
        this.volume = volume;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ContractOrder setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public String getOrder_price_type() {
        return order_price_type;
    }

    public ContractOrder setOrder_price_type(String order_price_type) {
        this.order_price_type = order_price_type;
        return this;
    }

    public String getDirection() {
        return direction;
    }

    public ContractOrder setDirection(String direction) {
        this.direction = direction;
        return this;
    }

    public String getOffset() {
        return offset;
    }

    public ContractOrder setOffset(String offset) {
        this.offset = offset;
        return this;
    }

    public int getLever_rate() {
        return lever_rate;
    }

    public ContractOrder setLever_rate(int lever_rate) {
        this.lever_rate = lever_rate;
        return this;
    }

    public long getClient_order_id() {
        return client_order_id;
    }

    public ContractOrder setClient_order_id(long client_order_id) {
        this.client_order_id = client_order_id;
        return this;
    }

    public long getOrder_id() {
        return order_id;
    }

    public ContractOrder setOrder_id(long order_id) {
        this.order_id = order_id;
        return this;
    }

    public long getCreated_at() {
        return created_at;
    }

    public ContractOrder setCreated_at(long created_at) {
        this.created_at = created_at;
        return this;
    }

    public BigDecimal getTrade_volume() {
        return trade_volume;
    }

    public ContractOrder setTrade_volume(BigDecimal trade_volume) {
        this.trade_volume = trade_volume;
        return this;
    }

    public BigDecimal getTrade_turnover() {
        return trade_turnover;
    }

    public ContractOrder setTrade_turnover(BigDecimal trade_turnover) {
        this.trade_turnover = trade_turnover;
        return this;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public ContractOrder setFee(BigDecimal fee) {
        this.fee = fee;
        return this;
    }

    public BigDecimal getTrade_avg_price() {
        return trade_avg_price;
    }

    public ContractOrder setTrade_avg_price(BigDecimal trade_avg_price) {
        this.trade_avg_price = trade_avg_price;
        return this;
    }

    public BigDecimal getMargin_frozen() {
        return margin_frozen;
    }

    public ContractOrder setMargin_frozen(BigDecimal margin_frozen) {
        this.margin_frozen = margin_frozen;
        return this;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public ContractOrder setProfit(BigDecimal profit) {
        this.profit = profit;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public ContractOrder setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getOrder_source() {
        return order_source;
    }

    public ContractOrder setOrder_source(String order_source) {
        this.order_source = order_source;
        return this;
    }
}
