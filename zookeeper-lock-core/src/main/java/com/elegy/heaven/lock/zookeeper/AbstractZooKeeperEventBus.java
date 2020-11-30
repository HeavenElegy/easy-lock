package com.elegy.heaven.lock.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件总线抽象声明
 * @author lixiaoxi_v
 */
public abstract class AbstractZooKeeperEventBus implements Watcher {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 处理器映射
     */
    private Map<Event.EventType, List<EventHandler>> handlerMapping = new HashMap<>();

    public AbstractZooKeeperEventBus() {
        // 初始化处理器映射
        for (EventHandler eventHandler : getInitEventHandlers()) {
            if(logger.isDebugEnabled()) {
                logger.debug("init zookeeper lock event handler -> {type: {}, class: {}}",
                        eventHandler.support(),
                        eventHandler.getClass());
            }
            List<EventHandler> handlerList =
                    handlerMapping.computeIfAbsent(
                            eventHandler.support(),
                            key -> new ArrayList<>());
            handlerList.add(eventHandler);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if(logger.isDebugEnabled()) {
            logger.debug(">>> receive event -> {type: {}, path: {}}", event.getType(), event.getPath());
        }
        List<EventHandler> handlerList;
        if ((handlerList = handlerMapping.get(event.getType())) != null) {
            // 委托到声明的处理器处理
            handlerList.forEach(item -> item.process(event));
            if(logger.isDebugEnabled()) {
                logger.debug("<<< qualified event handler -> {}", handlerList);
            }
        }
    }

    /**
     * 添加事件
     *
     * @param lock 目标锁,不能为空
     */
    public void addWaiter(Event.EventType eventType, ZooKeeperReentrantLock lock) {
        if(logger.isDebugEnabled()) {
            logger.debug(">>> add waiter -> {type: {}, lock: {}}", eventType, lock.getPath());
        }
        if (handlerMapping.containsKey(eventType)) {
            // 委托到声明的处理器处理
            handlerMapping.get(eventType).forEach(item -> item.afterAddWaiter(eventType, lock));
            if(logger.isDebugEnabled()) {
                logger.debug("<<< invoke postAddWaiter -> {}", handlerMapping.get(eventType));
            }
        }
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

        /**
         * 添加等待队列(并不是队列)后调用
         */
        void afterAddWaiter(Event.EventType eventType, ZooKeeperReentrantLock lock);
    }

    /**
     * 获取初始化处理器
     */
    public abstract List<EventHandler> getInitEventHandlers();

}
