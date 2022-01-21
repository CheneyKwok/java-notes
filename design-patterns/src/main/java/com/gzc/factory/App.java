package com.gzc.factory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    public static void main(String[] args) {
        Coin copperCoin = CoinFactory.getCoin(CoinType.COPPER);
        Coin goldCoin = CoinFactory.getCoin(CoinType.GOLD);
        log.info(copperCoin.getDescription());
        log.info(goldCoin.getDescription());
    }
}
