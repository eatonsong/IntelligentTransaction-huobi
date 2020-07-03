package com.soft.strategy;

import com.soft.strategy.bo.Spread3BO;
import com.soft.support.SymbolEnum;
import com.soft.support.TransConst;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

public class TransStrategy {

    /**
     * 三分法策略
     * @param
     * @param
     * @param
     * @param
     * @param
     * @return 三种币交易数量
     * @tips 购买数量和卖出
     */
    public static Map<String,BigDecimal> spread3(Spread3BO s){
        //初始拥有1u
        Map<String,BigDecimal> m = new HashMap();
        if(s.getType() == 0){
            // u/通用币价 = m1（通用币数量）
            BigDecimal m1 = s.getUsdt().divide(s.getBu(),10,ROUND_HALF_DOWN);
            //购买通用币数量
            m.put(TransConst.TradeType.bu,m1.setScale(SymbolEnum.getSymbolEnum(s.getbName()).getAmountScale(),ROUND_HALF_DOWN));
            m1 = m1.multiply(TransConst.fee);

            // 山寨币量
            BigDecimal m2 = m1.divide(s.getSb(),10,ROUND_HALF_DOWN);
            //购买山寨币量
            m.put(TransConst.TradeType.sb,m2.setScale(SymbolEnum.getSymbolEnum(s.getsName()).getAmountScale(),ROUND_HALF_DOWN));
            m2 = m2.multiply(TransConst.fee);

            BigDecimal result = m2.multiply(s.getSu());
            //卖出山寨币量
            m.put(TransConst.TradeType.su,m2.setScale(SymbolEnum.getSymbolEnum(s.getsName()).getAmountScale(),ROUND_HALF_DOWN));
            result = result.multiply(TransConst.fee);

            BigDecimal percent = result.subtract(s.getUsdt()).divide(s.getUsdt()).multiply(BigDecimal.valueOf(100));
            m.put(TransConst.TradeType.percent,percent);


            return m;

        }else{
            // u/山寨币价 = m3（购买山寨币数量）
            BigDecimal m3 = s.getUsdt().divide(s.getSu(),10,ROUND_HALF_DOWN);
            m.put(TransConst.TradeType.su,m3.setScale(SymbolEnum.getSymbolEnum(s.getsName()).getAmountScale(),ROUND_HALF_DOWN));
            m3 = m3.multiply(TransConst.fee);

            // m3*山寨币/通用币 = m2(通用币量)
            BigDecimal m2 = m3.multiply(s.getSb());
            //卖出山寨币数量
            m.put(TransConst.TradeType.sb,m3.setScale(SymbolEnum.getSymbolEnum(s.getsName()).getAmountScale(),ROUND_HALF_DOWN));
            m2 = m2.multiply(TransConst.fee);

            // 通用币换为u
            BigDecimal result = m2.multiply(s.getBu());
            //卖出通用币数量
            m.put(TransConst.TradeType.bu,m2.setScale(SymbolEnum.getSymbolEnum(s.getbName()).getAmountScale(),ROUND_HALF_DOWN));
            result = result.multiply(TransConst.fee);

            // 结果*100作为百分数
            BigDecimal percent = result.subtract(s.getUsdt()).divide(s.getUsdt()).multiply(BigDecimal.valueOf(100));
            m.put(TransConst.TradeType.percent,percent);

            return m;
        }

    }

    public static void main(String[] args) {
        BigDecimal b = new BigDecimal("0.00097137");

        System.out.println(b.setScale(SymbolEnum.btc.getAmountScale(),ROUND_HALF_DOWN));
    }

}
