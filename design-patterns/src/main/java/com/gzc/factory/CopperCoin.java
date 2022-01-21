package com.gzc.factory;

/**
 * 铜币实现
 */
public class CopperCoin implements Coin {

    static final String DESCRIPTION = "This is a copper coin";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
