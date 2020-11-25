package com.elegy.heaven.lock.zookeeper;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * @author lixiaoxi_v
 * @date 2020-11-17 19:22:31
 */
public class ZooKeeperReentrantLockTest {

    private ZooKeeperReentrantLock reentrantZooLock;
    public static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

    @Before
    public void before() {
        ZooKeeperLockSupport.init("127.0.0.1:2181", 20 * 1000, new DefaultOwnerGenerator());
        reentrantZooLock = new ZooKeeperReentrantLock("/lock_123");
    }

    /**
     * 一般的测试
     */
    @Test
    public void test01() {
        for (int i = 0; i < 10; i++) {
            System.out.println("尝试获取锁...");
            reentrantZooLock.lock();
            System.out.println("获取锁成功!!!");
            reentrantZooLock.unlock();
        }
    }

    @Test
    public void test02() throws InterruptedException {
        CyclicBarrier startBarrier = new CyclicBarrier(10);
        CountDownLatch endLatch = new CountDownLatch(10);
        int[] count = new int[]{0};
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " 进入等待状态...");
                    startBarrier.await();
                    System.out.println(Thread.currentThread().getName() + " 开始执行!");
                    for (int j = 0; j < 1000; j++) {
                        reentrantZooLock.lock();
                        count[0] = count[0] + 1;
                        reentrantZooLock.unlock();
                    }
                    System.out.println(Thread.currentThread().getName() + " 处理完成");
                    endLatch.countDown();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        endLatch.await();
        System.out.println("操作结果: " + count[0]);
    }

    @Test
    public void test03() throws InterruptedException {
        CyclicBarrier startBarrier = new CyclicBarrier(10);
        CountDownLatch endLatch = new CountDownLatch(10);
        int[] count = new int[]{0};
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " 进入等待状态...");
                    startBarrier.await();
                    System.out.println(Thread.currentThread().getName() + " 开始执行!");
                    for (int j = 0; j < 500; j++) {
                        System.out.println("当前线程 -> " + Thread.currentThread().getName());
                        reentrantZooLock.lock();
                        count[0] = count[0] + 1;
                        reentrantZooLock.lock();
                        count[0] = count[0] + 1;
                        reentrantZooLock.unlock();
                        reentrantZooLock.unlock();
                    }
                    System.out.println(Thread.currentThread().getName() + " 处理完成");
                    endLatch.countDown();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        endLatch.await();
        System.out.println("操作结果: " + count[0]);
    }

}
