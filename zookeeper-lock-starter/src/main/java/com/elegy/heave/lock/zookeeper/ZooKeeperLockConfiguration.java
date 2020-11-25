package com.elegy.heave.lock.zookeeper;

import com.elegy.heaven.lock.zookeeper.DefaultOwnerGenerator;
import com.elegy.heaven.lock.zookeeper.OwnerGenerator;
import com.elegy.heaven.lock.zookeeper.ZooKeeperLockSupport;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author lixiaoxi_v
 * @date 2020-11-19 10:25:00
 */
@Configuration
@ConditionalOnClass(ZooKeeper.class)
@ConditionalOnProperty(
        prefix = ZooKeeperLockProperties.PROPERTIES_PREFIX,
        name = ZooKeeperLockProperties.PROPERTIES_NAME_ENABLED,
        havingValue = "true",
        matchIfMissing = false)
@EnableConfigurationProperties(ZooKeeperLockProperties.class)
public class ZooKeeperLockConfiguration {

    @Configuration(proxyBeanMethods = false)
    public static class DefaultOwnerGeneratorConfiguration {
        @Bean
        @ConditionalOnMissingBean(OwnerGenerator.class)
        public OwnerGenerator<?> defaultOwnerGenerator() {
            return new DefaultOwnerGenerator();
        }
    }

    @Configuration(proxyBeanMethods = false)
    public static class ZooKeeperLockSupportConfiguration {

        private ZooKeeperLockProperties zookeeperLockProperties;
        private OwnerGenerator<?> ownerGenerator;

        public ZooKeeperLockSupportConfiguration(ZooKeeperLockProperties zookeeperLockProperties,
                                                 OwnerGenerator<?> ownerGenerator) {
            this.zookeeperLockProperties = zookeeperLockProperties;
            this.ownerGenerator = ownerGenerator;
        }

        @PostConstruct
        public void init() {
            ZooKeeperLockSupport.init(
                    zookeeperLockProperties.getConnectString(),
                    zookeeperLockProperties.getSessionTimeOut().toMillis(),
                    ownerGenerator);
        }
    }
}
