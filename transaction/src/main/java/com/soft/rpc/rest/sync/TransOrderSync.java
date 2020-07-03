package com.soft.rpc.rest.sync;


import com.huobi.client.SyncRequestClient;
import com.huobi.client.model.Order;
import com.huobi.client.model.enums.AccountType;
import com.huobi.client.model.enums.OrderState;
import com.huobi.client.model.enums.OrderType;
import com.huobi.client.model.request.NewOrderRequest;
import com.huobi.client.model.request.OpenOrderRequest;
import com.soft.support.TransConst;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Component
public class TransOrderSync {
    protected static final Logger logger = LoggerFactory.getLogger(TransOrderSync.class);

    SyncRequestClient syncRequestClient = SyncRequestClient.create(
            TransConst.AccessKey, TransConst.SecretKey);


    public void getOpenOrders(String symbol){
        OpenOrderRequest openOrderRequest = new OpenOrderRequest(symbol,AccountType.SPOT);
        List<Order> orders = syncRequestClient.getOpenOrders(openOrderRequest);
        for (Order orderInfo:orders){
            logger.info("Id: " + orderInfo.getOrderId());
            logger.info("Type: " + orderInfo.getType());
            logger.info("Status: " + orderInfo.getState());
            logger.info("Amount: " + orderInfo.getAmount());
            logger.info("Price: " + orderInfo.getPrice());
            logger.info("=========== ");
        }
    }

    public long createOrder(String symbol,BigDecimal amount,BigDecimal price,String orderType){
        NewOrderRequest newOrderRequest = new NewOrderRequest(
                symbol,
                AccountType.SPOT,
                OrderType.valueOf(orderType),
                amount,
                price);

        // Place a new limit order.
        long orderId = syncRequestClient.createOrder(newOrderRequest);
        logger.info("---- The new order created ----");
        return orderId;
    }

    public Order getOrder(String symbol, long orderId){
        // Get the order detail for order created above.
        Order orderInfo = syncRequestClient.getOrder(symbol, orderId);
        return orderInfo;
    }

    public void cancelOrder(String symbol, long orderId){
        // Cancel above order.
        syncRequestClient.cancelOrder(symbol, orderId);
    }

    public static void main(String[] args) {
        OrderState o1 = OrderState.CANCELED;
    }


}
