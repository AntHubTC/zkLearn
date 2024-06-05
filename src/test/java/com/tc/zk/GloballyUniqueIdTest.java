package com.tc.zk;

import org.apache.zookeeper.KeeperException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GloballyUniqueIdTest {
    private final static Logger log = LoggerFactory.getLogger(GloballyUniqueIdTest.class);

    @Test
    public void genIdTest() throws InterruptedException, KeeperException {
        String ip = "127.0.0.1:2181";
        GloballyUniqueId globallyUniqueId = new GloballyUniqueId(ip);
        for (int i = 0; i < 2000; i++) {
            log.info(globallyUniqueId.getUniqueID());
        }
    }
}
