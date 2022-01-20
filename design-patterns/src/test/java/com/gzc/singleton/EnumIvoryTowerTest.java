package com.gzc.singleton;


public class EnumIvoryTowerTest extends SingletonTest<EnumIvoryTower> {
    public EnumIvoryTowerTest() {
        super(() -> EnumIvoryTower.INSTANCE);
    }
}
