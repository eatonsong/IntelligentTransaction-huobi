package com.soft.support;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    private static  final AtomicInteger POOL_SEQ = new AtomicInteger(1);
    private final AtomicInteger threadNum;
    private final String prefix;
    private final boolean daemon;
    private final ThreadGroup group;

    public NamedThreadFactory(){
        this("pool-" + POOL_SEQ.getAndIncrement(),false);
    }
    public NamedThreadFactory(String prefix){
        this(prefix,false);
    }
    public NamedThreadFactory(String prefix,boolean daemon){
        this.threadNum = new AtomicInteger(1);
        this.prefix = prefix + "-thread-";
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager();
        this.group = s ==null ?Thread.currentThread().getThreadGroup(): s.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = this.prefix +this.threadNum.getAndIncrement();
        Thread ret = new Thread(this.group,r,name,0L);
        ret.setDaemon(this.daemon);
        return ret;
    }

    public ThreadGroup getThreadGroup(){
        return this.group;
    }
}
