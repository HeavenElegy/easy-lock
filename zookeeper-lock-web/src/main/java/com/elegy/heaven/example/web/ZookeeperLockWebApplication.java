package com.elegy.heaven.example.web;

import com.elegy.heave.lock.zookeeper.EnableZookeeperLock;
import com.elegy.heaven.lock.zookeeper.ZooKeeperReentrantLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableZookeeperLock
@SpringBootApplication
public class ZookeeperLockWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZookeeperLockWebApplication.class, args);
    }

    @GetMapping("/lock")
    public void lock(){
        ZooKeeperReentrantLock lock = new ZooKeeperReentrantLock("a2");
        lock.lock();
    }

}
