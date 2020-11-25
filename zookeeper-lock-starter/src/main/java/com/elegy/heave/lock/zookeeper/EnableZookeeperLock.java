package com.elegy.heave.lock.zookeeper;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lixiaoxi_v
 * @date 2020-11-25 10:30:18
 */
//@Retention(RetentionPolicy.RUNTIME)
//@Target(ElementType.TYPE)
//@Documented
//@Import(ZookeeperLockRegistrar.class)
public @interface EnableZookeeperLock {
}
