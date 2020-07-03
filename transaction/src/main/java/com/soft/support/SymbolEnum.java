package com.soft.support;

public enum SymbolEnum {
    usdt(0,6),
    btc(0,6),
    eos(0,4),
    eth(0,4),
    ltc(1,4),
    pai(1,2),
    cvc(1,2),
    ae(1,2),
    zil(1,2),
    dta(1,2),
    trx(1,2),
    egt(1,2),
    hit(1,2),
    xzc(1,2),
    wxt(1,2),
    itc(1,2),
    dcr(1,2),
    nano(1,2),
    unknown(1,2);

    //是否为山寨币 0否
    public int value;
    public int amountScale;

    SymbolEnum(int value,int amountScale) {
        this.value = value;
        this.amountScale = amountScale;
    }

    public int getValue() {
        return value;
    }


    public int getAmountScale() {
        return amountScale;
    }

    //根据symbol获取SymbolEnum
    public static SymbolEnum getSymbolEnum(String symbol){
        for(SymbolEnum symbolEnum:SymbolEnum.values()){
            if(symbolEnum.name().equals(symbol)){
                return symbolEnum;
            }
        }
        return unknown;
    }



}
