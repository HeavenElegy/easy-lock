package com.elegy.heaven.lock.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * zoo锁事件总线
 */
public abstract class AbstractZooKeeperEventBus implements Watcher {

    /**
     * 处理器映射
     */
    private Map<Event.EventType, List<EventHandler>> handlerMapping = new HashMap<>();

    public AbstractZooKeeperEventBus() {
        // 初始化处理器映射
        for (EventHandler eventHandler : getInitEventHandlers()) {
            List<EventHandler> handlerList;
            if (Objects.isNull(handlerList = handlerMapping.get(eventHandler.support()))) {
                handlerList = new ArrayList<>();
            }
            handlerList.add(eventHandler);
            handlerMapping.put(eventHandler.support(), handlerList);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
        List<EventHandler> handlerList;
        if ((handlerList = handlerMapping.get(event.getType())) != null) {
            // 委托到声明的处理器处理
            handlerList.forEach(item -> item.process(event));
        }
    }

    /**
     * 添加事件
     *
     * @param lock 目标锁,不能为空
     */
    public void addEvent(Event.EventType eventType, ZooKeeperReentrantLock lock) {
        if (handlerMapping.containsKey(eventType)) {
            // 委托到声明的处理器处理
            handlerMapping.get(eventType).forEach(item -> item.postCreateEvent(eventType, lock));
        }
//        ZooKeeperLockSupport.zookeeperHolder.addWatch(lock.getPath(), this);
    }

    /**
     * 事件处理器
     */
    public interface EventHandler extends Watcher {
        /**
         * 事件处理器支持的类型
         */
        Event.EventType support();

        /**
         * 当支持的事件类型发生时,进行调用
         *
         * @param event zookeeper原始事件
         */
        @Override
        void process(WatchedEvent event);

        void postCreateEvent(Event.EventType eventType, ZooKeeperReentrantLock lock);
    }

    /**
     * 获取初始化处理器
     */
    public abstract List<EventHandler> getInitEventHandlers();

}
