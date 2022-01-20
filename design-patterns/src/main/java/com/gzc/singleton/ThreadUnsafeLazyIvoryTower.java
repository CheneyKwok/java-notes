package com.gzc.singleton;

/**
 * 线程不安全的懒汉时单例实现
 */
public final class ThreadUnsafeLazyIvoryTower {

    private ThreadUnsafeLazyIvoryTower() {
    }

    private static ThreadUnsafeLazyIvoryTower INSTANCE;

    public static ThreadUnsafeLazyIvoryTower getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThreadUnsafeLazyIvoryTower();
        }
        return INSTANCE;
    }
}
