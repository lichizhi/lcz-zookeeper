package com.lcz.study.config;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfig {

    ZooKeeper zk;

    @Before
    public void con() {
        zk = ZKUtils.getZk();
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

        WatchCallback watchCallback = new WatchCallback();
        watchCallback.setZk(zk);
        ConfData confData = new ConfData();
        watchCallback.setConfData(confData);

        watchCallback.aWait();

        while (true) {
            System.out.println(confData.getConf());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
