package com.soft.po;

public class TransAnalysisTrade {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trans_analysis_trade.trade_id
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    private Integer tradeId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trans_analysis_trade.strat_id
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    private Integer stratId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trans_analysis_trade.result_id
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    private Integer resultId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trans_analysis_trade.symbol
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    private String symbol;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column trans_analysis_trade.init_money
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    private String initMoney;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trans_analysis_trade.trade_id
     *
     * @return the value of trans_analysis_trade.trade_id
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    public Integer getTradeId() {
        return tradeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trans_analysis_trade.trade_id
     *
     * @param tradeId the value for trans_analysis_trade.trade_id
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trans_analysis_trade.strat_id
     *
     * @return the value of trans_analysis_trade.strat_id
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    public Integer getStratId() {
        return stratId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trans_analysis_trade.strat_id
     *
     * @param stratId the value for trans_analysis_trade.strat_id
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    public void setStratId(Integer stratId) {
        this.stratId = stratId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trans_analysis_trade.result_id
     *
     * @return the value of trans_analysis_trade.result_id
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    public Integer getResultId() {
        return resultId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trans_analysis_trade.result_id
     *
     * @param resultId the value for trans_analysis_trade.result_id
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    public void setResultId(Integer resultId) {
        this.resultId = resultId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trans_analysis_trade.symbol
     *
     * @return the value of trans_analysis_trade.symbol
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trans_analysis_trade.symbol
     *
     * @param symbol the value for trans_analysis_trade.symbol
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column trans_analysis_trade.init_money
     *
     * @return the value of trans_analysis_trade.init_money
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    public String getInitMoney() {
        return initMoney;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column trans_analysis_trade.init_money
     *
     * @param initMoney the value for trans_analysis_trade.init_money
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    public void setInitMoney(String initMoney) {
        this.initMoney = initMoney == null ? null : initMoney.trim();
    }
}