package com.gzc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class InterruptDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted())
                    break;
            }
        });
        TimeUnit.SECONDS.sleep(1);
        log.info("interrupt");
        t.interrupt();

    }
}
