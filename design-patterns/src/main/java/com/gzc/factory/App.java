package com.gzc.factory;

import lombok.extern.slf4j.Slf4j;

/**
 * 静态简单工厂
 * 提供一个静态方法来创建和返回不同类的对象，以隐藏实现逻辑并使客户端代码专注于使用而不是对象的初始化和管理。
 * CoinFactory 是工厂类，它提供了一个静态方法来创建不同类型的硬币
 */
@Slf4j
public class App {

    public static void main(String[] args) {
        Coin copperCoin = CoinFactory.getCoin(CoinType.COPPER);
        Coin goldCoin = CoinFactory.getCoin(CoinType.GOLD);
        log.info(copperCoin.getDescription());
        log.info(goldCoin.getDescription());
    }
}
