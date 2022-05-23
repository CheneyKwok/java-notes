package com.gzc.netty.component.promise;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class JdkFutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<Integer> future = service.submit(() -> {
            log.info("执行计算");
            Thread.sleep(1000);
            return 50;
        });

        log.info("等待结果");
        log.info("结果是 {}", future.get());
    }
}
