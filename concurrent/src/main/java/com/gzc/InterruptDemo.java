package com.gzc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class InterruptDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                boolean flag = Thread.currentThread().isInterrupted();
                log.info("t1 interrupt flag = {}", flag);
                if (flag = Thread.currentThread().isInterrupted()){
                    // 打断正常的线程会将打断标记设置为 true
                    log.info("t1 interrupt flag = {}", true);
                    break;
                }
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                // 打断阻塞的线程会清楚打断标记 ( park 除外)
                log.info("t2 interrupt flag = {}", Thread.currentThread().isInterrupted());
            }
        });
        t1.start();
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        log.info("interrupt");
        t1.interrupt();
        t2.interrupt();

    }
}
