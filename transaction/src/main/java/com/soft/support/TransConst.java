package com.soft.support;

import java.math.BigDecimal;

/**
 * 常量类
 */
public class TransConst {

    public static final String AccessKey = "778e491d-afwo04df3f-df9f33f3-xxxx";
    public static final String SecretKey = "010121f0-0c322882-e0210617-xxxx";

    //交易费率(剩余)
    public static final BigDecimal fee = BigDecimal.valueOf(0.998);

    //交易类型
    public static final class TradeType{
        //山寨币转usdt
        public static final String su = "su";
        //山寨币转btc
        public static final String sb = "sb";
        //btc转u
        public static final String bu = "bu";

        public static final String percent = "percent";

    }


    //策略类型
    public static final class Strategy{
        //3币互转
        public static final String spread3 = "spread3";

        //触发购买百分比
        public static final BigDecimal percent = BigDecimal.valueOf(0.2);
    }

    //事件状态
    public static final class EventState{
        public static final String init = "init";
        public static final String doing = "doing";
        public static final String end = "end";
        //已结算
        public static final String settled = "settled";

    }

    //预订单状态
    public static final class PrepareOrderState{
        public static final String init = "init";
        public static final String doing = "doing";
        public static final String end = "end";
        public static final String cancel = "cancel";

    }

    //杠杆
    public static final class LeverRate{
        public static final BigDecimal l20 = BigDecimal.valueOf(20);
        public static final BigDecimal l10 = BigDecimal.valueOf(10);
        public static final BigDecimal l5 = BigDecimal.valueOf(5);
        public static final BigDecimal l1 = BigDecimal.valueOf(1);

    }



}
