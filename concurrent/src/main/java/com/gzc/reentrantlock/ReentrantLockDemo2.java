package com.gzc.reentrantlock;

import com.gzc.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 可打断
 */
@Slf4j
public class ReentrantLockDemo2 {

    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.info("启动...");
            try {
                log.info("尝试获取锁...");
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                log.info("获取锁失败, 返回");
                e.printStackTrace();
                return;
            }
            try {
                log.info("获得了锁");
            } finally {
                lock.unlock();
            }
        }, "t1");

        lock.lock();
        t1.start();
        try {
            Sleeper.sleep(1);
            t1.interrupt();
            log.info("执行打断");
        }finally {
            lock.unlock();
        }
    }
}
