package com.gzc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class JoinDemo {
    static int r = 0;

    public static void main(String[] args) {
        test1();
    }

    private static void test1() {

        Thread t1 = new Thread(() -> System.out.println("t1"), "t1");
        Thread t2 = new Thread(() -> {
            try {
                t1.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("t2");
        }, "t2");
        Thread t3 = new Thread(() -> {
            try {
                t2.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("t3");
        }, "t3");

        t3.start();
        t2.start();
        t1.start();
    }
}
