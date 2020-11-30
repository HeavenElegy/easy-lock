package com.elegy.heaven.lock.zookeeper;

import org.apache.zookeeper.WatchedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 默认事件总线
 *
 * @author lixiaoxi_v
 * @date 2020-11-17 14:44:39
 */
public class DefaultZooKeeperEventBus extends AbstractZooKeeperEventBus {

    private ZooKeeperHolder zooKeeperHolder;

    @Override
    public List<EventHandler> getInitEventHandlers() {
        // TODO: 2020/11/30 这里暂时这么写
        NodeDeleteEventHandler nodeDeleteEventHandler = new NodeDeleteEventHandler();
//        new Thread(nodeDeleteEventHandler, "NodeDeleteCompensationThread").start();
        return Collections.singletonList(nodeDeleteEventHandler);
    }

    /**
     * 节点删除事件处理器
     */
    private class NodeDeleteEventHandler implements EventHandler, Runnable {
        private static final long INTERVAL = 2000L;

        /**
         * 路径到锁的映射<br>
         */
        private Map<String, Set<ZooKeeperReentrantLock>> pathToLock = new HashMap<>();
        /**
         * 互斥映射
         */
        private Map<String, Object> mutexMapping = new HashMap<>();


        @Override
        public Event.EventType support() {
            return Event.EventType.NodeDeleted;
        }

        /**
         * @param event zookeeper原始事件
         */
        @Override
        public void process(WatchedEvent event) {
            doProcess(event.getPath());
        }

        @Override
        public void afterAddWaiter(Event.EventType eventType, ZooKeeperReentrantLock lock) {
            String path = lock.getPath();
            Object mutex = mutexMapping.get(path);
            if (mutex == null) {
                synchronized (mutexMapping) {
                    mutex = mutexMapping.get(path);
                    if (mutex == null) {
                        mutex = new Object();
                        mutexMapping.put(path, mutex);
                        pathToLock.put(path, new HashSet<>());
                    }
                }
            }
            synchronized (mutex) {
                pathToLock.get(path).add(lock);
            }
        }

        /**
         * 实际处理方法
         * // TODO: 2020/11/30 可能需要调用zk的监视移除接口，后续补充
         */
        private void doProcess(String path) {
            if (logger.isDebugEnabled()) {
                logger.debug("processing path {} by {}", path, getClass());
            }
            // 同步下进行当前path初始化
            Object mutex = mutexMapping.get(path);
            Set<ZooKeeperReentrantLock> locks;
            if (mutex == null) {
                synchronized (mutexMapping) {
                    mutex = mutexMapping.get(path);
                    if (mutex == null) {
                        mutex = new Object();
                        mutexMapping.put(path, mutex);
                        pathToLock.put(path, new HashSet<>());
                    }
                }
            }

            // 同步下进行唤醒操作
            synchronized (mutex) {
                if ((locks = pathToLock.get(path)) == null || locks.isEmpty()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("processing spiked queue is empty");
                    }
                    mutexMapping.remove(path);
                    pathToLock.remove(path);
                    return;
                }
                ZooKeeperReentrantLock lock = locks.iterator().next();
                locks.remove(lock);
                if (logger.isDebugEnabled()) {
                    logger.debug("processing use {}", lock);
                    logger.debug("current waiter lock {}", locks);
                }
                // 唤醒线程，不做实际释放操作
                lock.sync.release(ZooKeeperReentrantLock.SKIP_RELEASE);
                // 清理数据
                if (locks.isEmpty()) {
                    mutexMapping.remove(path);
                    pathToLock.remove(path);
                }
            }
        }

        /**
         * 用于补偿客户端单方等待问题
         */
        @Override
        public void run() {
            if (logger.isDebugEnabled()) {
                logger.debug("{} started", Thread.currentThread().getName());
            }
            for (; ; ) {
                try {
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                for (String path : pathToLock.keySet()) {
                    boolean exists = zooKeeperHolder.exists(path);
                    if (!exists) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("node path not existed now! -> {path: {}}", path);
                        }
                        // 直接调用删除回调，触发锁唤醒
                        // TODO: 2020/11/30 有多线程下的阻塞超时问题
                        doProcess(path);
                    }
                }
            }
        }
    }

    public void setZooKeeperHolder(ZooKeeperHolder zooKeeperHolder) {
        this.zooKeeperHolder = zooKeeperHolder;
    }
}
