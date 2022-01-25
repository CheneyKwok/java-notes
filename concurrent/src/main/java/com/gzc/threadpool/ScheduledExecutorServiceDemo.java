package com.gzc.threadpool;

import com.gzc.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ScheduledExecutorServiceDemo {

    public static void main(String[] args) {
//        test1();
//        test2();
//        test3();
        LocalDateTime now = LocalDateTime.now();
        // 每周五 10:04:00 触发
        LocalDateTime time = now.withHour(10).withMinute(4).withSecond(0).withNano(0).with(DayOfWeek.FRIDAY);
        if (now .compareTo(time) > 0) {
            time = time.plusWeeks(1);
        }
        long initialDelay = Duration.between(now, time).toMillis();
        long delay = 1000 * 60 * 60 * 24 * 7;
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.scheduleWithFixedDelay(() -> log.info("running..."), initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    private static void test3() {
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
