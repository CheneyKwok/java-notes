package com.gzc.singleton;

/**
 * 基于内部类实现的懒汉式线程安全单例实现
 * 内部类的引用不早于 getInstance() 的调用，且是线程安全的
 *
 */
public final class InitializingOnInnerClassLazyIvoryTower {

    private InitializingOnInnerClassLazyIvoryTower() {
    }

    public static InitializingOnInnerClassLazyIvoryTower getInstance() {
        return HelperHolder.INSTANCE;
    }

    /**
     * 提供延迟加载的单实例
     */
    private static class HelperHolder {
        private static final InitializingOnInnerClassLazyIvoryTower INSTANCE = new InitializingOnInnerClassLazyIvoryTower();
    }
}
