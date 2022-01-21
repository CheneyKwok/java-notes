package com.gzc.singleton;

/**
 * 线程安全的双重检查锁定的单例实现
 *
 * 使用 volatile 锁定，禁止指令重排
 *
 * 只有初始化的时候才会加锁，后续获取不会加锁
 */
public final class ThreadSafeDoubleCheckLockingLazyIvoryTower {

    private static volatile ThreadSafeDoubleCheckLockingLazyIvoryTower INSTANCE;

    private ThreadSafeDoubleCheckLockingLazyIvoryTower() {
        // 防止通过反射调用实例化
        if (INSTANCE != null) {
            throw new IllegalStateException("Already initialized");
        }
    }

    public static ThreadSafeDoubleCheckLockingLazyIvoryTower getInstance() {
        ThreadSafeDoubleCheckLockingLazyIvoryTower result = INSTANCE;

        if (result == null) {
            synchronized (ThreadSafeDoubleCheckLockingLazyIvoryTower.class) {
                result = INSTANCE;
                if (result == null) {
                    INSTANCE = result = new ThreadSafeDoubleCheckLockingLazyIvoryTower();
                }
            }
        }
        return result;
    }

    /**
     * 防反序列化
     */
    public Object readResolve() {
        return INSTANCE;
    }
}
