package com.gzc.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

@Slf4j
public class SemaphoreDebugDemo {

    private static Semaphore semaphore = new Semaphore(0);

    private static class Thread1  extends Thread {
        @Override
        public void run() {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Thread2  extends Thread {
        @Override
        public void run() {
            semaphore.release();
            log.info("t2 .....");
        }
    }


    public static void main(String[] args) throws InterruptedException {

        Thread1 t1 = new Thread1();
        Thread1 t2 = new Thread1();
        Thread2 t3 = new Thread2();
        Thread2 t4 = new Thread2();
        t1.setName("t1");
        t2.setName("t2");
        t3.setName("t3");
        t4.setName("t4");
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();

    }
}
