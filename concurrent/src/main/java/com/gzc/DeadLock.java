package com.gzc;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeadLock {
    public static void main(String[] args) {
        Object A = new Object();
        Object B = new Object();
        new Thread(() -> {
            synchronized (A){
                log.info("lock A");
                Sleeper.sleep(1);
                synchronized (B){
                    log.info("lock B");
                }
            }
        }, "t1").start();
        new Thread(() -> {
            synchronized (B){
                log.info("lock B");
                Sleeper.sleep(0.5);
                synchronized (A){
                    log.info("lock A");
                }
            }
        }, "t2").start();
    }

}
