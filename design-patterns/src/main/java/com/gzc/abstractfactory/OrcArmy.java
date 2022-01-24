package com.gzc.abstractfactory;

public class OrcArmy implements Army {

    static final String DESCRIPTION = "这是一只兽人军队";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
