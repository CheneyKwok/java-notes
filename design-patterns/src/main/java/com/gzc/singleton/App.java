package com.gzc.singleton;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    public static void main(String[] args) {

        IvoryTower ivoryTower1 = IvoryTower.getInstance();
        IvoryTower ivoryTower2 = IvoryTower.getInstance();
        log.info("ivoryTower1={}", ivoryTower1);
        log.info("ivoryTower2={}", ivoryTower2);

        ThreadSafeLazyIvoryTower threadSafeIvoryTower1 = ThreadSafeLazyIvoryTower.getInstance();
        ThreadSafeLazyIvoryTower threadSafeIvoryTower2 = ThreadSafeLazyIvoryTower.getInstance();
        log.info("threadSafeIvoryTower11={}", threadSafeIvoryTower1);
        log.info("threadSafeIvoryTower2={}", threadSafeIvoryTower2);

        EnumIvoryTower enumIvoryTower1 = EnumIvoryTower.INSTANCE;
        EnumIvoryTower enumIvoryTower2 = EnumIvoryTower.INSTANCE;
        log.info("enumIvoryTower1={}", enumIvoryTower1);
        log.info("enumIvoryTower2={}", enumIvoryTower2);

        ThreadSafeDoubleCheckLockingLazyIvoryTower dcl1 = ThreadSafeDoubleCheckLockingLazyIvoryTower.getInstance();
        log.info(dcl1.toString());
        ThreadSafeDoubleCheckLockingLazyIvoryTower dcl2 = ThreadSafeDoubleCheckLockingLazyIvoryTower.getInstance();
        log.info(dcl2.toString());

        InitializingOnInnerClassLazyIvoryTower demandHolderIdiom = InitializingOnInnerClassLazyIvoryTower.getInstance();
        log.info(demandHolderIdiom.toString());
        InitializingOnInnerClassLazyIvoryTower demandHolderIdiom2 = InitializingOnInnerClassLazyIvoryTower.getInstance();
        log.info(demandHolderIdiom2.toString());

    }
}
