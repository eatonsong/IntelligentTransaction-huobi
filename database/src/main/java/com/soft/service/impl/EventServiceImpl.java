package com.soft.service.impl;

import com.soft.mapper.TransEventMapper;
import com.soft.mapper.TransOrderMapper;
import com.soft.mapper.TransPrepareOrderMapper;
import com.soft.po.TransEvent;
import com.soft.po.TransOrder;
import com.soft.po.TransPrepareOrder;
import com.soft.service.EventService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class EventServiceImpl implements EventService{

    @Resource
    private TransEventMapper eventMapper;
    @Resource
    private TransOrderMapper orderMapper;
    @Resource
    private TransPrepareOrderMapper prepareOrderMapper;

    @Override
    public void insertEvent() {

        TransEvent e = new TransEvent();
        e.setMethod("aa");
        e.setState("0");
        eventMapper.insert(e);
        TransPrepareOrder p = new TransPrepareOrder();
        p.setEventId(e.getEventId());
        p.setCreateTime(new Date());
        p.setHuoOrderId("123dd");
        prepareOrderMapper.insert(p);

        TransOrder od = new TransOrder();
        od.setEventId(e.getEventId());
        od.setPrepareOrderId(p.getPrepareOrderId());
        orderMapper.insert(od);


    }
}
