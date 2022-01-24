package com.gzc.abstractfactory;

public class ElfKing implements King {
    static final String DESCRIPTION = "这是一位精灵王";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
