package com.tc.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;


/**
 * 相关文档参考：https://juejin.cn/column/6964703461547966471
 */
public class ZKConnectTest {
    protected static final Logger logger = LoggerFactory.getLogger(ZKConnectTest.class);
    protected ZooKeeper zooKeeper;

    @Before
    public void before() throws IOException, InterruptedException {
        final String IP = "127.0.0.1:2181";  // ip及端口
        final int TIMEOUT = 5000;  // 超时时间，毫秒为单位

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(IP, TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                switch (watchedEvent.getType()) {
                    case NodeCreated:
                        logger.info("节点创建了");
                        break;
                    case NodeDataChanged:
                        logger.info("节点数据被修改了");
                        break;
                    case NodeDeleted:
                        logger.info("节点被删除了");
                        break;
                    case None: {
                        Event.KeeperState state = watchedEvent.getState();
                        logger.info(state.toString());

                        if (state == Event.KeeperState.SyncConnected) {
                            logger.info("建立了连接！");
                            countDownLatch.countDown();
                        } else if (state == Event.KeeperState.Disconnected) {
                            logger.info("断开了连接！");
                        } else if (state == Event.KeeperState.Expired) {
                            logger.info("连接超时！");
                        } else if (state == Event.KeeperState.AuthFailed) {
                            logger.info("认证失败！");
                        }

                        break;
                    }
                }
            }
        });
        countDownLatch.await();
    }

    @Test
    public void test() {
        logger.info(zooKeeper.toString());
    }

    @After
    public void after() throws InterruptedException {
        zooKeeper.close();
    }
}
