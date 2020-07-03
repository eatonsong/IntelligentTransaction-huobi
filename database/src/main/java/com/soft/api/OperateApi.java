package com.soft.api;

import com.soft.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class OperateApi {

    @Autowired
    private EventService eventService;


    @GetMapping("/hello")
    public String test(){
        return "Hello@";
    }

    @GetMapping("/insert")
    public void insert(){
        eventService.insertEvent();
    }
/*    public static void main(String[] args) {
        SubscriptionOptions options = new SubscriptionOptions();
        options.setUri("wss://api.huobi.pro");
        SubscriptionClient subscriptionClient = SubscriptionClient.create(TransConst.AccessKey, TransConst.SecretKey, options);
        AtomicReference<BigDecimal> btcusdt = null;
        subscriptionClient.subscribe24HTradeStatisticsEvent("btcusdt", (statisticsEvent) -> {
*//*            System.out.println();
            System.out.println("Timestamp: " + statisticsEvent.getData().getTimestamp());
            System.out.println("High: " + statisticsEvent.getData().getHigh());
            System.out.println("Low: " + statisticsEvent.getData().getLow());
            System.out.println("Open: " + statisticsEvent.getData().getOpen());
            System.out.println("Close: " + statisticsEvent.getData().getClose());
            System.out.println("Volume: " + statisticsEvent.getData().getVolume());*//*
            btcusdt.set(statisticsEvent.getData().getClose());
        });
    }*/

    public static void main(String[] args) {
        double i = 5650.1*0.45/(2*1.1+3*1.05)*1.1 + 5650.1*0.55/5;
        System.out.printf(String.valueOf(i));
    }

}
