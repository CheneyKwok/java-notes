package com.gzc.factory;

/**
 * 金币实现
 */
public class GoldCoin implements Coin {

    static final String DESCRIPTION = "This is a gold coin";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
