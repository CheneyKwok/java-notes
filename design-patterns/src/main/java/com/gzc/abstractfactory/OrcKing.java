package com.gzc.abstractfactory;

public class OrcKing implements King {

    static final String DESCRIPTION = "这是一位兽人统领";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
