package com.gzc.singleton;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ThreadSafeDoubleCheckLockingLazyIvoryTowerTest extends SingletonTest<ThreadSafeDoubleCheckLockingLazyIvoryTower>{

    public ThreadSafeDoubleCheckLockingLazyIvoryTowerTest() {
        super(ThreadSafeDoubleCheckLockingLazyIvoryTower::getInstance);
    }

    /**
     * 测试通过反射创建新实例
     */
    @Test
    void testCreatingNewInstanceByRefection() throws Exception {
        ThreadSafeDoubleCheckLockingLazyIvoryTower.getInstance();
        Constructor<ThreadSafeDoubleCheckLockingLazyIvoryTower> constructor = ThreadSafeDoubleCheckLockingLazyIvoryTower.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Assertions.assertThrows(InvocationTargetException.class, () -> constructor.newInstance((Object[]) null));
    }
}
