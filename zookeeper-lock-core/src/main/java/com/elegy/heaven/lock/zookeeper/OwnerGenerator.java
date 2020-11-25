package com.elegy.heaven.lock.zookeeper;

import java.io.Serializable;

/**
 * @author lixiaoxi_v
 * @date 2020-11-23 11:22:35
 */
public interface OwnerGenerator<T extends OwnerGenerator.Owner> {

    T getOwner();

    /**
     * @author lixiaoxi_v
     * @date 2020-11-23 11:22:59
     */
    abstract static class Owner implements Serializable {
        /**
         * 必须实现此方法,用于辨别当前锁占用者是否与申请者相同
         */
        @Override
        public abstract boolean equals(Object obj);
    }
}
