package com.lcz.study.config;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZKUtils {

    private static final String zkHost = "node01:2181,node02:2181,node03:2181,node04:2181/testConf";

    private static ZooKeeper zk;

    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static ZooKeeper getZk() {
        DefaultWatch watch = new DefaultWatch();
        watch.setCountDownLatch(countDownLatch);
        try {
            zk = new ZooKeeper(zkHost, 1000, watch);
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return zk;
    }


}
