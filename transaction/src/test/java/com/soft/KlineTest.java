package com.soft;

import com.huobi.client.SubscriptionClient;
import com.huobi.client.model.Candlestick;
import com.huobi.client.model.enums.CandlestickInterval;
import com.soft.mongo.CandlestickModel;
import com.soft.mongo.MongodbUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KlineTest {



    @Test
    public void save(){
        SubscriptionClient subscriptionClient = SubscriptionClient.create();
        subscriptionClient.subscribeAllCandlestickEvent("eosusdt", CandlestickInterval.MIN1, (list) -> {
            for(Object c:list){
                if(c!=null){
                    Candlestick data = (Candlestick)c;
                    CandlestickModel model = new CandlestickModel();
                    model.setAmount(data.getAmount());
                    model.setClose(data.getClose());
                    model.setCount(data.getCount());
                    model.setTimestamp(data.getTimestamp());
                    model.setHigh(data.getHigh());
                    model.setLow(data.getLow());
                    model.setOpen(data.getOpen());
                    model.setVolume(data.getVolume());
                    MongodbUtils.save(model,"eosusdt");
                }
            }
        });
        while (true){

        }
    }
}
