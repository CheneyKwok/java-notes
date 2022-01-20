package com.gzc.singleton;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
abstract class SingletonTest<S> {

    private final Supplier<S> singletonInstanceMethod;

    protected SingletonTest(Supplier<S> singletonInstanceMethod) {
        this.singletonInstanceMethod = singletonInstanceMethod;
    }

    /**
     * 测试多个调用在同一个线程中返回同一个对象
     */
    @Test
    void testMultipleCallsReturnTheSameObjectInSameThread() {
        S instance1 = this.singletonInstanceMethod.get();
        S instance2 = this.singletonInstanceMethod.get();
        S instance3 = this.singletonInstanceMethod.get();
        Assertions.assertSame(instance1, instance2);
        Assertions.assertSame(instance2, instance3);
        Assertions.assertSame(instance1, instance3);
    }

    /**
     * 测试多个调用在不同线程中返回同一个对象
     */
    @Test
    void testMultipleCallsReturnTheSameObjectInDifferentThreads() {
        Assertions.assertTimeout(Duration.ofMillis(10000), () -> {
            Supplier<S> instanceMethod = this.singletonInstanceMethod;

            // 创建 10000 个任务并在回调中实现化单例类
            List<Callable<S>> tasks = IntStream
                    .range(0, 10000)
                    .<Callable<S>>mapToObj(i -> instanceMethod::get)
                    .collect(Collectors.toList());
            // 最多使用 8 个并发线程来处理任务
            ExecutorService executorService = Executors.newFixedThreadPool(8);
            List<Future<S>> results = executorService.invokeAll(tasks);

            // 等待所有线程完成
            S expectedInstance = instanceMethod.get();
            for (Future<S> result : results) {
                S instance = result.get();
                Assertions.assertNotNull(instance);
                Assertions.assertSame(expectedInstance, instance);
            }
            // 关闭执行器
            executorService.shutdown();
        });
    }
}
