package com.elegy.heaven.lock.zookeeper;

import org.apache.zookeeper.Watcher;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author lixiaoxi_v
 * @date 2020-11-16 16:20:27
 */
public class ZooKeeperReentrantLock extends ZooKeeperLockSupport implements Lock {

    public static final String SEPARATOR = "/";
    public static final int SKIP_RELEASE = -1;
    final Sync sync;
    private final String path;

    public ZooKeeperReentrantLock(String key) {
        this.sync = new Sync();
        if(!key.startsWith(SEPARATOR)) {
            key = SEPARATOR + key;
        }
        this.path = key;
    }

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1, false);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        // TODO: 2020/11/17 需要处理
        throw new UnsupportedOperationException();
    }

    public String getPath() {
        return path;
    }


    class Sync extends AbstractQueuedSynchronizer {

        @Override
        protected boolean tryAcquire(int acquires) {
            return tryAcquire(acquires, true);
        }

        /**
         * 扩展获取方法,添加一个回调函数
         *
         * @param addWatcher 占用是非是否添加
         */
        protected boolean tryAcquire(int acquires, boolean addWatcher) {
            boolean result = casAddNode(acquires);
            if (!result && addWatcher) {
                zooKeeperLockEventBus.addWaiter(Watcher.Event.EventType.NodeDeleted, ZooKeeperReentrantLock.this);
            }
            return result;
        }

        @Override
        protected boolean tryRelease(int releases) {
            return SKIP_RELEASE == releases || casReduceCount(releases);
        }

        /**
         * 仿造cas进行占用操作
         */
        private boolean casAddNode(int acquires) {
            boolean result;
            if (zookeeperHolder.exists(path)) {
                // 存在,进行比较
                DefaultZooKeeperReentrantData data = zookeeperHolder.getData(path);
                if (Objects.isNull(data)) {
                    // 不存在,进行创建
                    result = zookeeperHolder.create(path, new DefaultZooKeeperReentrantData(acquires, getOwner()));
                } else {
                    // 存在,返回比较结果
                    result = data.getOwner().equals(getOwner());
                }
            } else {
                // 不存在,尝试创建
                result = zookeeperHolder.create(path, new DefaultZooKeeperReentrantData(acquires, getOwner()));
            }
            if(!result) {
                zookeeperHolder.toWatch(path);
            }
            return result;
        }

        /**
         * 减小计数
         */
        private boolean casReduceCount(int releases) {
            // 不存在直接返回
            if (!zookeeperHolder.exists(path)) {
                return false;
            }

            // 存在,进行比较
            DefaultZooKeeperReentrantData data = zookeeperHolder.getData(path);
            if (Objects.isNull(data)) {
                return false;
            }

            // 不是拥有者
            if (!data.getOwner().equals(getOwner())) {
                return false;
            }

            int count = data.getCount();
            int residue;
            if ((residue = count - releases) < 0) {
                throw new IllegalArgumentException("释放计数过大 -> [" + count + ":" + releases + "]");
            } else if (residue == 0) {
                // 释放锁
                zookeeperHolder.delete(path);
                return true;
            } else {
                // 更新计数
                data.setCount(count + 1);
                zookeeperHolder.setData(path, data);
                return true;
            }
        }
    }

    private OwnerGenerator.Owner getOwner() {
        return ZooKeeperLockSupport.ownerGenerator.getOwner();
    }
}
