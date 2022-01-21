package com.gzc.singleton;

/**
 * 线程安全的懒汉式单例实现
 *
 * 每次获取单例都会加锁
 */
public class ThreadSafeLazyIvoryTower {

    private static ThreadSafeLazyIvoryTower INSTANCE;

    private ThreadSafeLazyIvoryTower() {
        // 防止通过反射调用实例化
        if (INSTANCE != null) {
            throw new IllegalStateException("Already initialized");
        }
    }

    public static synchronized ThreadSafeLazyIvoryTower getInstance() {
        ThreadSafeLazyIvoryTower result = INSTANCE;
        if (result == null) {
            INSTANCE = result = new ThreadSafeLazyIvoryTower();
        }
        return result;
    }
}
