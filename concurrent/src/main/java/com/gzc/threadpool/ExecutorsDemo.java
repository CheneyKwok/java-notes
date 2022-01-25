package com.gzc.threadpool;

import com.gzc.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

@Slf4j
public class ExecutorsDemo {

    public static void main(String[] args) {
//        testNewSingleThreadExecutor();
        SynchronousQueue<Integer> queue = new SynchronousQueue<>();
        new Thread(() -> {
            try {
                log.info("putting...{}", 1);
                queue.put(1);
                log.info("putted...{}", 1);
                log.info("putting...{}", 2);
                queue.put(2);
                log.info("putted...{}", 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Sleeper.sleep(1);

        new Thread(() -> {
            try {
                log.info("take..{}", 1);
                queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void testNewSingleThreadExecutor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            log.info("1");
            int i = 1 / 0;
        });
        executor.execute(() -> log.info("2"));
        executor.execute(() -> log.info("3"));
    }
}
