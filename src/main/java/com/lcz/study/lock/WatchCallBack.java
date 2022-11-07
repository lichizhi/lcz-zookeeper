package com.lcz.study.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WatchCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {

    private ZooKeeper zk;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private String threadName;
    private String pathName;

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void tryLock() {
        // 创建各自的临时序列节点
        zk.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "abc");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock() {
        // 删除节点
        try {
            zk.delete(pathName, -1);
            System.out.println(threadName + " unLock");
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "abc");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            case PersistentWatchRemoved:
                break;
        }
    }

    // 创建临时序列节点回调
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if (name != null) {
            // name里包含 /
            pathName = name;
            System.out.println(threadName + " create node: " + name);
            // 获取所有children
            zk.getChildren("/", false, this, "abc");
        }
    }

    // 获取children回调
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        Collections.sort(children);
        int index = children.indexOf(pathName.substring(1));
        if (index == 0) {
            // 如果是第一个
            try {
                System.out.println(threadName + " locked");
                zk.setData("/", threadName.getBytes(), -1);
                countDownLatch.countDown();
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // 不是第一个，那么就watch前一个
            zk.exists("/" + children.get(index - 1), this, this, "abc");
        }

    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

    }
}
