package com.gzc.singleton;

public class ThreadUnsafeLazyIvoryTowerTest extends SingletonTest<ThreadUnsafeLazyIvoryTower>{

    public ThreadUnsafeLazyIvoryTowerTest() {
        super(ThreadUnsafeLazyIvoryTower::getInstance);
    }
}
