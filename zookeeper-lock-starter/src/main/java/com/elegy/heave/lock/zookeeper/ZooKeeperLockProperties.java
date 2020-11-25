package com.elegy.heave.lock.zookeeper;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author lixiaoxi_v
 * @date 2020-11-19 10:01:25
 */
@ConfigurationProperties(prefix = ZooKeeperLockProperties.PROPERTIES_PREFIX)
public class ZooKeeperLockProperties {

    public static final String PROPERTIES_PREFIX = "zookeeper.lock";
    public static final String PROPERTIES_NAME_ENABLED = "enabled";

    /**
     * zookeeper地址
     */
    private String connectString = "127.0.0.1:2181";
    /**
     * 超时时间,默认30秒
     */
    private Duration sessionTimeOut = Duration.ofSeconds(30);
    /**
     * 是否启用
     */
    private boolean enabled;

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public Duration getSessionTimeOut() {
        return sessionTimeOut;
    }

    public void setSessionTimeOut(Duration sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
