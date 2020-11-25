package com.elegy.heave.lock.zookeeper;

import com.elegy.heaven.lock.zookeeper.ZooKeeperReentrantLock;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EasyLockStarterApplicationTests {
    private ZooKeeperReentrantLock lock = new ZooKeeperReentrantLock("a");

    @Test
    void contextLoads() {
        for (int i = 0; i < 10; i++) {
            lock.lock();
        }
    }

}
