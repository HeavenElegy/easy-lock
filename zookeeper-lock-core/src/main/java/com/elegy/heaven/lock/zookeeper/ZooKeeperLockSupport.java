package com.elegy.heaven.lock.zookeeper;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

/**
 * @author lixiaoxi_v
 * @date 2020-11-18 18:51:14
 */
public abstract class ZooKeeperLockSupport implements Lock {
    protected static AbstractZooKeeperEventBus zooKeeperLockEventBus;
    protected static ZooKeeperHolder zookeeperHolder;
    protected static OwnerGenerator<? extends OwnerGenerator.Owner> ownerGenerator;

    public static void init(String connectString,
                            long sessionTimeout,
                            OwnerGenerator<? extends OwnerGenerator.Owner> ownerGenerator) {
        if (Objects.isNull(zooKeeperLockEventBus)) {
            try {
                zooKeeperLockEventBus = new DefaultZooKeeperEventBus();
                zookeeperHolder = new ZooKeeperHolder(new ZooKeeper(
                        connectString,
                        (int) sessionTimeout,
                        zooKeeperLockEventBus));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        ZooKeeperLockSupport.ownerGenerator = ownerGenerator;
    }
}
