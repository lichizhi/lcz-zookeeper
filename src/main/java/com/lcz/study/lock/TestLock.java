package com.lcz.study.lock;

import com.lcz.study.config.ZKUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLock {

    ZooKeeper zk;

    @Before
    public void con() {
        zk = ZKUtils.getZk("node01:2181,node02:2181,node03:2181,node04:2181/testLock");
    }

    @After
    public void close() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void lock() {

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String threadName = Thread.currentThread().getName();
                    WatchCallBack watchCallBack = new WatchCallBack();
                    watchCallBack.setThreadName(threadName);
                    watchCallBack.setZk(zk);

                    // 抢锁
                    watchCallBack.tryLock();
                    // 业务
                    System.out.println(threadName + " is working...");
                    // 释放锁
                    watchCallBack.unLock();
                }
            }).start();

        }

        while (true) {

        }

    }

}
