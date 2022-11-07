package com.lcz.study.config;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfig {

    ZooKeeper zk;

    @Before
    public void con() {
        zk = ZKUtils.getZk("node01:2181,node02:2181,node03:2181,node04:2181/testConf");
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
    public void getConf() {

        WatchCallBack watchCallback = new WatchCallBack();
        watchCallback.setZk(zk);
        ConfData confData = new ConfData();
        watchCallback.setConfData(confData);

        watchCallback.aWait();

        while (true) {
            if (confData.getConf() == null) {
                System.out.println("miss conf");
                watchCallback.aWait();
            } else {
                System.out.println(confData.getConf());
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
