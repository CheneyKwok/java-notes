package com.gzc.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CompletableFutureDemo {

    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws Exception {

        // 无返回值的异步执行
        CompletableFuture.runAsync(() -> {
            log.info("当前线程：{}", Thread.currentThread().getName());
            int i = 1;
            log.info("运行结果：{}", i);
        }, executor);

        // 有返回值的异步执行
        CompletableFuture<Integer> result = CompletableFuture.supplyAsync(() -> {
            log.info("当前线程：{}", Thread.currentThread().getName());
            int i = 2;
            log.info("运行结果：{}", i);
            return i;
        }, executor);
        log.info("返回结果：{}", result.get());

        // 感知异步执行的结果
        result = CompletableFuture
                .supplyAsync(() -> {
                    log.info("当前线程：{}", Thread.currentThread().getName());
                    int i = 3;
                    log.info("运行结果：{}", i);
                    return i;
                }, executor)
                .whenComplete((res, ex) -> {
                    log.info("异步任务完成，结果是：{}, 异常：{}", res, ex);
                })
                .exceptionally(throwable -> 10);
        log.info("返回结果：{}", result.get());

        // 对异步执行的结果进行处理
        result = CompletableFuture.supplyAsync(() -> {
                    log.info("当前线程：{}", Thread.currentThread().getName());
                    int i = 4;
                    log.info("运行结果：{}", i);
                    return i;
                }, executor)
                .handle((res, ex) -> {
                    if (res != null) {
                        return res * 2;
                    }
                    if (ex != null) {
                        return 0;
                    }
                    return res;
                });
        log.info("返回结果：{}", result.get());

        // 直接执行下一个异步任务
        CompletableFuture.supplyAsync(() -> {
                    log.info("当前线程：{}", Thread.currentThread().getName());
                    int i = 5;
                    log.info("运行结果：{}", i);
                    return i;
                }, executor)
                .thenRunAsync(() -> {
                    log.info("当前线程：{}", Thread.currentThread().getName());
                    int i = 6;
                    log.info("运行结果：{}", i);
                }, executor);

        // 接受上一个任务的结果并执行下一个异步任务，无返回值
        CompletableFuture.supplyAsync(() -> {
                    log.info("当前线程：{}", Thread.currentThread().getName());
                    int i = 7;
                    log.info("运行结果：{}", i);
                    return i;
                }, executor)
                .thenAcceptAsync(res -> {
                    log.info("当前线程：{}", Thread.currentThread().getName());
                    int i = res * 2;
                    log.info("运行结果：{}", i);
                }, executor);

        // 接受上一个任务的结果并执行下一个异步任务，有返回值
        result = CompletableFuture.supplyAsync(() -> {
                    log.info("当前线程：{}", Thread.currentThread().getName());
                    int i = 8;
                    log.info("运行结果：{}", i);
                    return i;
                }, executor)
                .thenApplyAsync(res -> {
                    log.info("当前线程：{}", Thread.currentThread().getName());
                    int i = res * 2;
                    log.info("运行结果：{}", i);
                    return i;
                }, executor);
        log.info("返回结果：{}", result.get());

        // 两个任务都完成后执行新的任务
        CompletableFuture<Integer> task1 = CompletableFuture.supplyAsync(() -> {
            log.info("task1 线程：{}", Thread.currentThread().getName());
            log.info("task1 完成");
            return 100;
        }, executor);
        CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
            log.info("task2 线程：{}", Thread.currentThread().getName());
            log.info("task2 完成");
            return 200;
        }, executor);
        task1.runAfterBothAsync(task2, () -> {
            log.info("task3 执行");
        }, executor);

        task1.thenAcceptBothAsync(task2, (res1, res2) -> {
            log.info("task4 执行, task1 结果：{}，task2 结果：{}", res1, res2);
        }, executor);

        result = task1.thenCombineAsync(task2, (res1, res2) -> {
            log.info("task5 执行, task1 结果：{}，task2 结果：{}", res1, res2);
            return res1 + res2;
        }, executor);
        log.info("task5返回结果：{}", result.get());

        // 两个任务任意一个完成后执行新的任务
        task1.runAfterEitherAsync(task2, () -> {
            log.info("task6 执行");
        }, executor);

        task1.acceptEitherAsync(task2, (res) -> {
            log.info("task7 执行, task1 或 task2 结果：{}", res);
        }, executor);

        result = task1.applyToEitherAsync(task2, (res) -> {
            log.info("task8 执行, task1 或 task2 结果：{}", res);
            return res * 2;
        }, executor);
        log.info("task8返回结果：{}", result.get());

        // 多任务组合
        CompletableFuture.allOf(task1, task2).get();
        CompletableFuture<Object> res = CompletableFuture.anyOf(task1, task2);
        log.info("多任务组合返回的结果：{}", res.get().toString());
    }
}
