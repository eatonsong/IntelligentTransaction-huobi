package com.soft.rpc.rest.async;

import com.huobi.client.AsyncRequestClient;
import com.huobi.client.RequestOptions;
import com.huobi.client.SubscriptionClient;
import com.huobi.client.SyncRequestClient;
import com.huobi.client.model.Candlestick;
import com.huobi.client.model.TradeStatistics;
import com.huobi.client.model.enums.CandlestickInterval;

public class TransTradeAsync {
    public static void main(String[] args) {
        SubscriptionClient subscriptionClient = SubscriptionClient.create();
        subscriptionClient.subscribeAllCandlestickEvent("btcusdt", CandlestickInterval.MIN1, (list) -> {
            for(Object c:list){
                Candlestick data = (Candlestick)c;
                System.out.println();
                System.out.println("Timestamp: " + data.getTimestamp());
                System.out.println("High: " + data.getHigh());
            }
        });
    }
}
