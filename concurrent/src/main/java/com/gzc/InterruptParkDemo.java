package com.gzc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class InterruptParkDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            log.info("park.....");
            LockSupport.park();
            log.info("un park......");
            log.info("打断状态：{}", Thread.currentThread().isInterrupted());
//            log.info("打断状态：{}", Thread.interrupted());
            LockSupport.park();
            log.info("un park......");
        });

        t.start();
        TimeUnit.SECONDS.sleep(1);
        t.interrupt();
    }
}

