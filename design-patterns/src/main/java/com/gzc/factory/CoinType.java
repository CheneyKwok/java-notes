package com.gzc.factory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

/**
 * 硬币类型枚举
 */
@RequiredArgsConstructor
@Getter
public enum CoinType {

    COPPER(CopperCoin::new),

    GOLD(GoldCoin::new);

    private final Supplier<Coin> constructor;
}
