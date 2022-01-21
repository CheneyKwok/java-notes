package com.gzc.factory;

/**
 * 静态简单工厂
 */
public class CoinFactory {

    public static Coin getCoin(CoinType type) {
        return type.getConstructor().get();
    }
}
