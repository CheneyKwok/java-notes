package com.gzc.reentrantlock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.gzc.Sleeper.sleep;


@Slf4j
public class ReentrantLockDemo4 {

    static boolean hasCigarette = false;
    static boolean hasTakeout = false;
    static ReentrantLock lock = new ReentrantLock();
    static Condition cigaretteCondition = lock.newCondition();
    static Condition takeoutCondition = lock.newCondition();


    public static void main(String[] args) {
        new Thread(() -> {
            lock.lock();

            try {
                while (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        cigaretteCondition.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                log.debug("可以开始干活了");
            } finally {
                lock.unlock();
            }
            log.debug("有烟没？[{}]", hasCigarette);

        }, "小南").start();

        new Thread(() -> {
            lock.lock();
            try {
                log.debug("外卖送到没？[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        takeoutCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("可以开始干活了");
            } finally {
                lock.unlock();
            }

        }, "小女").start();

        sleep(1);
        new Thread(() -> {
            lock.lock();
            try {
                hasTakeout = true;
                log.debug("外卖到了噢！");
                takeoutCondition.signal();
            } finally {
                lock.unlock();
            }
        }, "送外卖的").start();
    }
}
