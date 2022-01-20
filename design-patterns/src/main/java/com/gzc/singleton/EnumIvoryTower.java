package com.gzc.singleton;

/**
 * 基于枚举的线程安全单例实现
 */
public enum EnumIvoryTower {

    INSTANCE;

    @Override
    public String toString() {
        return getDeclaringClass().getCanonicalName() + "@" + hashCode();
    }
}
