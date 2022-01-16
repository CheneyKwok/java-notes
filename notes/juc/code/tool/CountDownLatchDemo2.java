package juc.code.tool;


import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CountDownLatchDemo2 {

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(10);
        AtomicInteger sum = new AtomicInteger(0);
        Random random = new Random();
        ExecutorService pool = Executors.newFixedThreadPool(10, (task) -> new Thread(task, "t" + (sum.getAndIncrement())));
        String[] all = new String[10];
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            pool.submit(() -> {
                for (int j = 0; j <= 100; j++) {
                    all[finalI] = Thread.currentThread().getName() + "[" + j + "%]";
                    try {
                        Thread.sleep(random.nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // \r 后面的输出覆盖前面的输出
                    System.out.print("\r" + Arrays.toString(all));
                }
                latch.countDown();
            });
        }
        try {
            latch.await();
            System.out.println("\n游戏开始");
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
