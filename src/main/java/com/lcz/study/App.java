package com.lcz.study;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        System.out.println( "Hello World!" );

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        // zk是有session概念的，没有连接池的概念
        // sessionTimeout是客户端连接断开后，3秒后客户端创建的临时节点会删除
        // watch有两类：
        // 1.new的时候传入的，这个watch它是session级别的，跟path、node没有关系
        // 2.watch的注册只发生在读
        final ZooKeeper zk = new ZooKeeper("192.168.129.128:2181,192.168.129.129:2181,192.168.129.130:2181,192.168.129.131:2181",
                3000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                Event.KeeperState state = event.getState();
                Event.EventType type = event.getType();
//                String path = event.getPath();
                System.out.println(event.toString());

                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("sync connected...");
                        countDownLatch.countDown();
                        break;
                    case AuthFailed:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        break;
                    case Closed:
                        break;
                }

                switch (type) {
                    case None:
                        break;
                    case NodeCreated:
                        break;
                    case NodeDeleted:
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
        });

        countDownLatch.await();
        ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("connecting...");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("connected...");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        // 同步阻塞
        String pathName = zk.create("/ooxx", "oldData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println(pathName);

        final Stat stat = new Stat();
        byte[] data = zk.getData("/ooxx", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("getData: " + event.toString());
                try {
                    // 如果为true，这里的watch是默认watch，也就是new Zookeeper的时候注册的watch
                    // 如果为this，就是当前的
//                    zk.getData("/ooxx", true, stat);
                    zk.getData("/ooxx", this, stat);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);
        System.out.println(new String(data));

        // 触发回调
        Stat stat1 = zk.setData("/ooxx", "newData".getBytes(), stat.getVersion());
        Stat stat2 = zk.setData("/ooxx", "newData2".getBytes(), stat1.getVersion());

        // 异步方式
        System.out.println("async start...");
        zk.getData("/ooxx", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("async callback...");
                System.out.println(new String(data));
                System.out.println((String) ctx);
                System.out.println(path);
            }
        }, "abc");

        System.out.println("async over...");

        Thread.sleep(2000);
    }
}
