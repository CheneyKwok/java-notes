package com.gzc.reentrantlock;

import com.gzc.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.StampedLock;

public class StampedLockDemo {

    public static void main(String[] args) throws InterruptedException {
        DataContainStamped containStamped = new DataContainStamped(1);
        // 读-读
//        new Thread(() -> containStamped.read(1), "t1").start();
//        new Thread(() -> containStamped.read(1), "t2").start();
        // 读-写
        new Thread(() -> containStamped.read(1), "t1").start();
        Thread t2 = new Thread(() -> containStamped.write(2), "t2");
        t2.start();
        t2.join();
        new Thread(() -> containStamped.read(1), "t3").start();

    }
}

@Slf4j
class DataContainStamped {
    private Object data;
    private final StampedLock lock = new StampedLock();

    public DataContainStamped(Object data) {
        this.data = data;
    }

    public Object read(int readTime) {
        long stamp = lock.tryOptimisticRead();
        log.info("optimistic read locking....{}", stamp);
        if (lock.validate(stamp)) {
            log.info("optimistic read finish....{}", stamp);
            Sleeper.sleep(readTime);
            return data;
        }
        // 锁升级
        log.info("updating to read lock....{}", stamp);
        try {
            stamp = lock.readLock();
            log.info("read lock...{}", stamp);
            Sleeper.sleep(readTime);
            log.info("read finish...{}", stamp);
            return data;
        }finally {
            log.info("read unlock...{}", stamp);
            lock.unlockRead(stamp);
        }
    }

    public void write(Object data) {
        long stamp = lock.writeLock();
        log.info("write lock...{}", stamp);
        try {
            Sleeper.sleep(3);
            this.data = data;
        } finally {
            log.info("write unlock....{}", stamp);
            lock.unlockWrite(stamp);
        }
    }
}
