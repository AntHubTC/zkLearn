package com.tc.zk;

import cn.hutool.json.JSONUtil;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GetDataTest extends ZKConnectTest{
    @Test
    public void syncGetData() throws InterruptedException, KeeperException {
        Stat stat = new Stat();
        byte[] data = zooKeeper.getData("/tc/helloNode", false, stat);
        logger.info("data={}", new String(data));
        logger.info("stat={}", JSONUtil.toJsonPrettyStr(stat));
    }

    @Test
    public void asyncGetData() throws InterruptedException, KeeperException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper.getData("/tc/helloNode", false, (rc, path, ctx, bytes, stat) -> {
            logger.info(String.valueOf(rc));
            logger.info(path);
            logger.info(new String(bytes));
            logger.info("stat={}", JSONUtil.toJsonPrettyStr(stat));
            countDownLatch.countDown();
        }, null);
        countDownLatch.await(); // 拥塞
    }

    @Test
    public void testSyncChildren() throws InterruptedException, KeeperException {
        List<String> children = zooKeeper.getChildren("/", null);  // 此操作，类似于ls
        children.forEach(logger::info);  // 获取所有子节点的名称
    }


    @Test
    public void testAsyncChildren() throws InterruptedException, KeeperException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        zooKeeper.getChildren("/zookeeper", false, (rc, path, ctx, children) -> {
            logger.info(path);
            for (String childPath : children) {
                zooKeeper.getChildren(path+childPath, false, (rc2, path2, ctx2, children2) -> {
                    logger.info(path2);

                    countDownLatch.countDown();
                }, null);
            }
        }, null);

        countDownLatch.await();
    }
}
