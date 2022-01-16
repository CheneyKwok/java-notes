package juc.code.tool;

import juc.code.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CyclicBarrierDemo {

    public static void main(String[] args) {

        AtomicInteger count = new AtomicInteger(0);
        ExecutorService pool = Executors.newFixedThreadPool(5, (run) -> new Thread(run, "t" + count.getAndIncrement()));
        CyclicBarrier barrier = new CyclicBarrier(5, () -> {
            Sleeper.sleep(1);
            log.info("发护照和签证");
            pool.shutdown();
        });
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            pool.submit(() -> {
                // 模拟到达需要花费的时间
                try {
                    Thread.sleep(random.nextInt(2000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String name = Thread.currentThread().getName();
                log.info("{} 到达集合点", name);
                try {
                    barrier.await();
                    log.info("{} 开始旅行", name);
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
