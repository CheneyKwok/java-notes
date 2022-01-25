package com.gzc.tool;

import com.gzc.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

@Slf4j
public class SemaphoreDemo {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(0);
        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                try {
                    // 获取许可
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    log.info("run...");
                    Sleeper.sleep(1);
                    log.info("end");
                } finally {
                    // 释放许可
                    semaphore.release();
                }
            }).start();
        }
    }

}
