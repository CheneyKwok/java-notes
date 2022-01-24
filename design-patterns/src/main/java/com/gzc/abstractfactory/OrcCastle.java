package com.gzc.abstractfactory;

public class OrcCastle implements Castle {

    static final String DESCRIPTION = "这是一个兽人城堡";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
