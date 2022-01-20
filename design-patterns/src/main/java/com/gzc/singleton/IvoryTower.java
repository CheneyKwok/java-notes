package com.gzc.singleton;

/**
 * 饿汉式线程安全单例实现
 */
public final class IvoryTower {

    private IvoryTower() {
    }

    private static final IvoryTower INSTANCE = new IvoryTower();

    public static IvoryTower getInstance() {
        return INSTANCE;
    }
}
