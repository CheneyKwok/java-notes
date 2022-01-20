package com.gzc.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static juc.code.Sleeper.sleep;

@Slf4j
public class CountDownLatchDemo1 {

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(3);
        ExecutorService pool = Executors.newFixedThreadPool(3);
        pool.submit(() -> {
            log.info("begin...");
            sleep(1);
            latch.countDown();
            log.info("end...{}", latch.getCount());
        });

        pool.submit(() -> {
            log.info("begin...");
            sleep(1.5);
            latch.countDown();
            log.info("end...{}", latch.getCount());
        });

        pool.submit(() -> {
            log.info("begin...");
            sleep(2);
            latch.countDown();
            log.info("end...{}", latch.getCount());
        });

        pool.submit(() -> {
            try {
                log.info("waiting....");
                latch.await();
                log.info("wait end");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
