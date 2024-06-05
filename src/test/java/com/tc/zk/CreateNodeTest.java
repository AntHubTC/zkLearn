package com.tc.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.junit.Test;

import java.nio.charset.Charset;

public class CreateNodeTest extends ZKConnectTest{
    @Test
    public void createPersistentNode() throws InterruptedException, KeeperException {
        byte[] data = "hello".getBytes(Charset.defaultCharset());
        zooKeeper.create("/tc", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        String path = zooKeeper.create("/tc/helloNode", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        logger.info("节点创建成功！path={}", path);
    }

    @Test
    public void createPersistentSeqNode() throws InterruptedException, KeeperException {
        byte[] data = "hello".getBytes(Charset.defaultCharset());
        // zooKeeper.create("/tc", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        String path = zooKeeper.create("/tc/helloNode2", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        logger.info("节点创建成功！path={}", path);
    }


    @Test
    public void createEphemeralLNode() throws InterruptedException, KeeperException {
        byte[] data = "hello".getBytes(Charset.defaultCharset());
        // zooKeeper.create("/tc", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        String path = zooKeeper.create("/tc/helloNode3", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        logger.info("节点创建成功！path={}", path);
    }
    @Test
    public void createEphemeralLSeqNode() throws InterruptedException, KeeperException {
        byte[] data = "hello".getBytes(Charset.defaultCharset());
        // zooKeeper.create("/tc", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        String path = zooKeeper.create("/tc/helloNode4", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        logger.info("节点创建成功！path={}", path);
    }
}
