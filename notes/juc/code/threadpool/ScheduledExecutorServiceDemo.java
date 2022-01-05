package juc.code.threadpool;

import juc.code.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ScheduledExecutorServiceDemo {

    public static void main(String[] args) {
//        test1();
//        test2();
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        log.info("start....");
        pool.scheduleWithFixedDelay(() -> {
            log.info("run...");
            Sleeper.sleep(2);
        }, 2, 1, TimeUnit.SECONDS);
    }

    private static void test2() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        log.info("start....");
        pool.scheduleAtFixedRate(() -> {
            log.info("run....");
            Sleeper.sleep(2);
        }, 1, 1, TimeUnit.SECONDS);
    }

    private static void test1() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        log.info("start...");
        pool.schedule(() -> log.info("run task1..."), 1, TimeUnit.SECONDS);
        pool.schedule(() -> log.info("run task2..."), 1, TimeUnit.SECONDS);
    }
}
