package com.gzc.singleton;


public class IvoryTowerTest extends SingletonTest<IvoryTower>{

    public IvoryTowerTest() {
        super(IvoryTower::getInstance);
    }
}
