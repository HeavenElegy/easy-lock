package com.elegy.heaven.lock.zookeeper;

import java.io.Serializable;

/**
 * @author lixiaoxi_v
 * @date 2020-11-16 16:25:19
 */
class DefaultZooKeeperReentrantData extends AbstractZooKeeperData implements Serializable {
    /**
     * 计数
     */
    private Integer count;
    /**
     * 拥有者
     */
    private OwnerGenerator.Owner owner;

    public DefaultZooKeeperReentrantData(Integer count, OwnerGenerator.Owner owner) {
        this.count = count;
        this.owner = owner;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public OwnerGenerator.Owner getOwner() {
        return owner;
    }

    public void setOwner(OwnerGenerator.Owner owner) {
        this.owner = owner;
    }


    @Override
    public String toString() {
        return "DefaultZooKeeperReentrantData{" +
                "count=" + count +
                ", owner=" + owner +
                '}';
    }
}
