package com.gzc.abstractfactory;

public class ElfArmy implements Army {

    static final String DESCRIPTION = "这是一只精灵军队";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
