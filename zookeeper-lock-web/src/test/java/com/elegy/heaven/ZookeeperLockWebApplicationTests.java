package com.elegy.heaven;

import com.elegy.heaven.lock.zookeeper.DefaultOwnerGenerator;
import com.elegy.heaven.lock.zookeeper.ZooKeeperLockSupport;
import com.elegy.heaven.lock.zookeeper.ZooKeeperReentrantLock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ZookeeperLockWebApplicationTests {

    /**
     * 初始化
     */
    @BeforeAll
    public static void before() {
        ZooKeeperLockSupport.init(
                "127.0.0.1:2181",
                20 * 1000,
                new DefaultOwnerGenerator());
    }

    @Test
    public void test01() {
        ZooKeeperReentrantLock lock = new ZooKeeperReentrantLock("key");
        lock.lock();
        // to do something
        lock.unlock();
    }

}
