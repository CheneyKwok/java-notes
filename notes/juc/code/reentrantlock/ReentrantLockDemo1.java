package juc.code.reentrantlock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入
 */
@Slf4j
public class ReentrantLockDemo1 {

    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {

        lock.lock();
        try {
            log.info("enter main");
            m1();
        }finally {
            lock.unlock();
        }
    }

    public static void m1() {

        lock.lock();
        try {
            log.info("enter m1");
            m2();
        }finally {
            lock.unlock();
        }
    }

    public static void m2() {

        lock.lock();
        try {
            log.info("enter m2");
        }finally {
            lock.unlock();
        }
    }
}
