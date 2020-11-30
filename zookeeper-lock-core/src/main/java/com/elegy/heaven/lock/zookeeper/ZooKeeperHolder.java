package com.elegy.heaven.lock.zookeeper;

import org.apache.commons.lang.SerializationUtils;
import org.apache.zookeeper.AddWatchMode;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;

/**
 * @author lixiaoxi_v
 * @date 2020-11-11 18:42:59
 */
public class ZooKeeperHolder {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ZooKeeper zooKeeper;
    public static final int MINUS_ONE = -1;

    public ZooKeeperHolder(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    /**
     * 创建节点
     */
    public boolean create(String key, AbstractZooKeeperData data) {

        try {
            if(logger.isDebugEnabled()) {
                logger.debug(">>> occupy node -> {path: {}, data: {}}", key, data);
            }
            zooKeeper.create(
                    key,
                    SerializationUtils.serialize(data),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
            if(logger.isDebugEnabled()) {
                logger.debug("<<< occupy node succeed -> {path: {}, data: {}}", key, data);
            }
            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            if(logger.isDebugEnabled()) {
                logger.error("<<< occupy node unsuccessful -> {path: {}, data: {}, err: {}}", key, data, e.getMessage());
            }
            return false;
        }
    }

    public boolean delete(String key) {
        try {
            zooKeeper.delete(key, MINUS_ONE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            return false;
        }
        return true;
    }

    public void toWatch(String key) {
        try {
            zooKeeper.addWatch(
                    key,
                    ZooKeeperLockSupport.zooKeeperLockEventBus,
                    AddWatchMode.PERSISTENT
            );
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeWatch(String key, Watcher watcher) {
        try {
            zooKeeper.removeWatches(
                    key,
                    watcher,
                    Watcher.WatcherType.Any,
                    false
            );
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends AbstractZooKeeperData> T getData(String key) {
        try {
            byte[] data = zooKeeper.getData(key, false, new Stat());
            if (Objects.isNull(data)) {
                return null;
            }
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (T) objectInputStream.readObject();
        } catch (KeeperException e) {
            if (e.code().equals(KeeperException.Code.NONODE)) {
                return null;
            } else {
                throw new RuntimeException(e);
            }
        } catch (InterruptedException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean setData(String key, AbstractZooKeeperData data) {
        try {
            zooKeeper.setData(key, SerializationUtils.serialize(data), -1);
            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            return false;
        }
    }

    public boolean exists(String key) {
        try {
            return Objects.nonNull(zooKeeper.exists(key, false));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            return false;
        }
    }
}
