package com.gzc.singleton;

public class ThreadSafeDoubleCheckLockingLazyIvoryTowerTest extends SingletonTest<ThreadSafeDoubleCheckLockingLazyIvoryTower>{

    public ThreadSafeDoubleCheckLockingLazyIvoryTowerTest() {
        super(ThreadSafeDoubleCheckLockingLazyIvoryTower::getInstance);
    }
}
