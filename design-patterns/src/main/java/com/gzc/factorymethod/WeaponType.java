package com.gzc.factorymethod;

import lombok.RequiredArgsConstructor;

/**
 * 武器类型枚举
 */
@RequiredArgsConstructor
public enum WeaponType {

    SHORT_SWORD("短剑"),
    SPEAR("矛"),
    AXE("斧子"),
    UNDEFINED("");

    private final String title;

    @Override
    public String toString() {
        return title;
    }
}
