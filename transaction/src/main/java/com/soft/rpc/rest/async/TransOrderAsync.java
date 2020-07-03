package com.soft.rpc.rest.async;

import com.huobi.client.AsyncRequestClient;
import com.huobi.client.AsyncResult;
import com.huobi.client.ResponseCallback;
import com.huobi.client.model.enums.AccountType;
import com.huobi.client.model.enums.OrderType;
import com.huobi.client.model.request.NewOrderRequest;
import com.soft.support.TransConst;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransOrderAsync {


    public static void main(String[] args) {
        AsyncRequestClient asyncRequestClient = AsyncRequestClient.create(TransConst.AccessKey,TransConst.SecretKey);

        NewOrderRequest newOrderRequest = new NewOrderRequest(
                "paiusdt",
                AccountType.SPOT,
                OrderType.SELL_LIMIT,
                BigDecimal.valueOf(1.0),
                BigDecimal.valueOf(1.0));
        asyncRequestClient.createOrder(newOrderRequest,(result)->{
            System.out.println(result.succeeded());
            System.out.println(result.getException());
            System.out.println(result.getData());
        });
    }
    public void test(){

    }



}
