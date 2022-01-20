package com.gzc;

import lombok.extern.slf4j.Slf4j;

import static com.gzc.Sleeper.sleep;

@Slf4j
public class ThreadStateDemo {

    public static void main(String[] args) {

        // NEW
        Thread t1 = new Thread(() -> log.info("running..."), "t1");

        // RUNNABLE
        Thread t2 = new Thread(() -> {
            while (true){}
        }, "t2");
        t2.start();

        // TERMINATED
        Thread t3 = new Thread(() -> log.info("running..."), "t3");
        t3.start();

        // TIMED_WAITING
        Thread t4 = new Thread(() -> {
            synchronized (ThreadStateDemo.class) {
                sleep(100000);
            }
        });
        t4.start();

        // WAITING
        Thread t5 = new Thread(() -> {
            try {
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t5");
        t5.start();

        // BLOCKED
        Thread t6 = new Thread(() -> {
            synchronized (ThreadStateDemo.class) {
                sleep(1000000);
            }
        }, "t6");
        t6.start();

        sleep(500);

        log.info("t1 state: {}", t1.getState());
        log.info("t2 state: {}", t2.getState());
        log.info("t3 state: {}", t3.getState());
        log.info("t4 state: {}", t4.getState());
        log.info("t5 state: {}", t5.getState());
        log.info("t6 state: {}", t6.getState());
    }

}
