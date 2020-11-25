package com.elegy.heaven.lock.zookeeper;

import org.apache.zookeeper.WatchedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lixiaoxi_v
 * @date 2020-11-17 14:44:39
 */
public class DefaultZooKeeperEventBus extends AbstractZooKeeperEventBus {

    @Override
    public List<EventHandler> getInitEventHandlers() {
        return Collections.singletonList(new NodeDeleteEventHandler());
    }

    /**
     * 节点删除事件处理器
     */
    private static class NodeDeleteEventHandler implements EventHandler {

        private ConcurrentMap<String, List<ZooKeeperReentrantLock>> pathToLock = new ConcurrentHashMap<>();

        @Override
        public Event.EventType support() {
            return Event.EventType.NodeDeleted;
        }

        /**
         * @param event zookeeper原始事件
         */
        @Override
        public void process(WatchedEvent event) {
            String path;
            List<ZooKeeperReentrantLock> locks;
            if ((locks = pathToLock.get(path = event.getPath())) == null || locks.isEmpty()) {
                return;
            }
            locks.remove(0).sync.release(ZooKeeperReentrantLock.SKIP_RELEASE);
            if (locks.isEmpty()) {
                pathToLock.remove(path);
            }
        }

        @Override
        public void postCreateEvent(Event.EventType eventType, ZooKeeperReentrantLock lock) {
            List<ZooKeeperReentrantLock> locks =
                    pathToLock.computeIfAbsent(
                            lock.getPath(),
                            s -> Collections.synchronizedList(new ArrayList<>()));
            locks.add(lock);
        }
    }
}
