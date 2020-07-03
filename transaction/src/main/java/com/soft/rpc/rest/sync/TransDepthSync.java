package com.soft.rpc.rest.sync;

import com.huobi.client.SyncRequestClient;
import com.huobi.client.model.DepthEntry;
import com.huobi.client.model.PriceDepth;
import org.springframework.stereotype.Component;

/**
 * 获取价格深度
 */
@Component
public class TransDepthSync {

    private SyncRequestClient syncRequestClient = SyncRequestClient.create();

    public PriceDepth getDepth(String symbol){
        return syncRequestClient.getPriceDepth(symbol, 3);
    }



}
