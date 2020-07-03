package com.soft.rpc.rest.sync;

import com.huobi.client.SyncRequestClient;
import com.huobi.client.model.Account;
import com.huobi.client.model.Balance;
import com.huobi.client.model.enums.AccountType;
import com.huobi.client.model.enums.BalanceType;
import com.huobi.client.model.enums.OrderType;
import com.soft.support.SymbolEnum;
import com.soft.support.TransCommon;
import com.soft.support.TransConst;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取用户信息
 */
@Component
public class TransAccountSync {


    /**
     * 获取剩余额度
     * @return
     */
    public Map<String,BigDecimal> getRemain(){
        List<Balance> balances = this.getBalance();
        Map m = new HashMap();
        EnumSet<SymbolEnum> weekSet = EnumSet.allOf(SymbolEnum.class);

        for (Balance b :balances){
            weekSet.forEach(w->{
                if(w.name().equals(b.getCurrency())){
                    if(b.getType() == BalanceType.TRADE){
                        m.put(w.name(),b.getBalance());
                    }
                }
            });

        }
        return m;
    }

    public static void main(String[] args) {
        System.out.println(new TransAccountSync().isRemainFull("paibtc",OrderType.SELL_LIMIT.name(),"909.17","0.00000215"));
    }
    /**
     * 查看余额是否充足
     * @param symbol
     * @param orderType
     * @param amount
     * @param price
     * @return
     */
    public boolean isRemainFull(String symbol,String orderType,String amount,String price){
        boolean result = false;
        if(amount == null || price == null || orderType == null){
            return false;
        }
        String costName = TransCommon.getCostBi(symbol,orderType);
        Map<String,BigDecimal> remains = this.getRemain();
        BigDecimal remain = remains.get(costName);
        BigDecimal amount1 = new BigDecimal(amount);
        BigDecimal price1 = new BigDecimal(price);
        BigDecimal costs;

        if(OrderType.SELL_LIMIT.name().equals(orderType)){
            //SELL_LIMIT 花费为持有币量
            costs = amount1;
        }else {
            //BUY_LIMIT 花费为价格*数量
            costs = amount1.multiply(price1);
        }
        if(remain == null || remain.compareTo(costs) == -1){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 获取所有币种账户余额
     * @return
     */
    public List<Balance> getBalance(){
        SyncRequestClient syncRequestClient = SyncRequestClient.create(TransConst.AccessKey,TransConst.SecretKey);
        AccountType accountType = AccountType.SPOT;
        Account account = syncRequestClient.getAccountBalance(accountType);
        List<Balance> balances = account.getBalances();
        return balances;
    }


}
