package com.gzc.singleton;

public class ThreadSafeLazyIvoryTowerTest extends SingletonTest<ThreadSafeLazyIvoryTower>{

    public ThreadSafeLazyIvoryTowerTest() {
        super(ThreadSafeLazyIvoryTower::getInstance);
    }
}
