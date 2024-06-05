package com.tc.zk;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class GloballyUniqueId implements Watcher, Closeable {

    private String ip = null;  // ip及端口
    private static Integer timeOut = 5000;  // 超时时间，毫秒为单位
    private final static CountDownLatch countDownLatch = new CountDownLatch(1);
    private final static Logger log = LoggerFactory.getLogger(GloballyUniqueId.class);
    private ZooKeeper zooKeeper = null;
    private String parentPath = "/uniqueId";

    private GloballyUniqueId() {
    }

    public GloballyUniqueId(String ip) {
        this(ip, timeOut);
    }

    public GloballyUniqueId(String ip, Integer timeOut) {
        this.ip = ip;
        GloballyUniqueId.timeOut = timeOut;
        initZK(ip, timeOut);
    }

    public GloballyUniqueId setParentPath(String parentPath) {
        this.parentPath = parentPath;
        return this;
    }

    /**
     * 初始化zookeeper
     *
     * @param ip
     * @param timeOut
     * @throws IOException
     * @throws InterruptedException
     */
    private void initZK(String ip, Integer timeOut) {
        try {
            zooKeeper = new ZooKeeper(ip, timeOut, this);
            countDownLatch.await();
        } catch (IOException | InterruptedException e) {
            log.error("初始化zookeeper失败");
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.None) {
            switch (watchedEvent.getState()) {
                case SyncConnected:
                    log.info("连接 " + ip + "成功");
                    countDownLatch.countDown();
                    break;
                case Disconnected:
                    log.error("连接 " + ip + "已断开");
                    break;
                case Expired:
                    log.error("连接 " + ip + "已超时，需要重新连接服务器端");
                    this.initZK(ip, timeOut);
                    break;
                case AuthFailed:
                    log.error("身份验证失败");
                    break;
            }
        }
    }

    /**
     * 重点！！！用于通过持久顺序节点，生成新的ID，并返回
     *
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String getUniqueID() throws KeeperException, InterruptedException {
        if (zooKeeper.exists(parentPath, null) == null) {
            zooKeeper.create(parentPath, "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        String path = zooKeeper.create(parentPath + parentPath, "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        return path.substring(2 * parentPath.length() + 1);
    }

    @Override
    public void close() {
        try {
            zooKeeper.close();
            log.info("zooKeeper已关闭");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}