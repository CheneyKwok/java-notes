package com.gzc.singleton;

public class InitializingOnInnerClassLazyIvoryTowerTest extends SingletonTest<InitializingOnInnerClassLazyIvoryTower> {

    public InitializingOnInnerClassLazyIvoryTowerTest() {
        super(InitializingOnInnerClassLazyIvoryTower::getInstance);
    }
}
