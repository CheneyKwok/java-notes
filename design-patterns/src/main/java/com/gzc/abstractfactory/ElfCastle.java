package com.gzc.abstractfactory;

public class ElfCastle implements Castle {

    static final String DESCRIPTION = "这是一个精灵城堡";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
