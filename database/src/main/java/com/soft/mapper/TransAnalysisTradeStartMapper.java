package com.soft.mapper;

import com.soft.po.TransAnalysisTradeStart;

public interface TransAnalysisTradeStartMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trans_analysis_trade_start
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    int deleteByPrimaryKey(Integer startId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trans_analysis_trade_start
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    int insert(TransAnalysisTradeStart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trans_analysis_trade_start
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    int insertSelective(TransAnalysisTradeStart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trans_analysis_trade_start
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    TransAnalysisTradeStart selectByPrimaryKey(Integer startId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trans_analysis_trade_start
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    int updateByPrimaryKeySelective(TransAnalysisTradeStart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trans_analysis_trade_start
     *
     * @mbg.generated Sat Oct 12 16:33:37 CST 2019
     */
    int updateByPrimaryKey(TransAnalysisTradeStart record);
}