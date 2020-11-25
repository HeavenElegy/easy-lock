package com.elegy.heaven.example.web.configuration;

import com.elegy.heaven.lock.zookeeper.OwnerGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lixiaoxi_v
 * @date 2020-11-24 18:27:10
 */
@Configuration
public class ZookeeperLockConfiguration {

    @Bean
    public OwnerGenerator<? extends OwnerGenerator.Owner> ownerGenerator() {
        return new MyOwnerGenerator();
    }

    private static class MyOwnerGenerator implements OwnerGenerator<MyOwner> {

        @Override
        public MyOwner getOwner() {
            return new MyOwner();
        }
    }

    private static class MyOwner extends OwnerGenerator.Owner {

        private String threadName;

        public MyOwner() {
            this.threadName = Thread.currentThread().getName();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MyOwner) {
                return ((MyOwner) obj).threadName.equals(this.threadName);
            } else {
                return false;
            }
        }
    }
}
