package com.gzc;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SyncModeDemo {

    static final Object lock = new Object();

    static boolean t2Runned = false;

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            synchronized (lock){
                while (!t2Runned) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("1");
            }

        }, "t1");

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                t2Runned = true;
                lock.notify();
                log.info("2");
            }
        }, "t2");

        t1.start();
        t2.start();
    }
}
