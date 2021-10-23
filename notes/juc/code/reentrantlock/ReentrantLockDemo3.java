package juc.code.reentrantlock;

import juc.code.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 锁超时
 */
@Slf4j
public class ReentrantLockDemo3 {

    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.info("启动...");
            log.info("尝试获取锁...");
            try {
                if (!lock.tryLock(2, TimeUnit.SECONDS)) {
                    log.info("获取不到锁");
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.info("获取不到锁");
                return;
            }
            try {
                log.info("获得了锁");
            } finally {
                log.info("释放锁");
                lock.unlock();
            }
        }, "t1");

        lock.lock();
        t1.start();
        try {
            Sleeper.sleep(1);
        }finally {
            lock.unlock();
        }
    }
}
