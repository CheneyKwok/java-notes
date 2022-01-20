package com.gzc.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class TaskCatchExceptionDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        test1();
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Future<Boolean> result = pool.submit(() -> {
            log.info("task");
            int i = 1 / 0;
            return true;
        });
        log.info("result:{}", result.get());

    }

    private static void test1() {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        pool.submit(() -> {
            try {
                log.info("task");
                int i = 1 / 0;
            } catch (Exception e) {
                log.error("error:", e);
            }
        });
    }
}
