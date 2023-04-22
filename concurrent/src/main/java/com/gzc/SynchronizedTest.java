package com.gzc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SynchronizedTest {


    public static void main(String[] args) {
//        Thread why = new Thread(new TicketConsumer(10), "why");
//        Thread mx = new Thread(new TicketConsumer(10), "mx");
//        why.start();
//        mx.start();
        AtomicInteger count = new AtomicInteger(0);

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("judge-pool-%d")
                .setUncaughtExceptionHandler((t, e) -> log.error("ThreadPool {} got exception {}", t, e))
                .build();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 1, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),threadFactory);
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                System.out.println(Thread.currentThread().getName());
                int j = 2 / 0;
            });

        }


    }

}


class TicketConsumer implements Runnable {

    private volatile static Integer ticket;

    public TicketConsumer(int ticket) {
        this.ticket = ticket;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println(Thread.currentThread().getName() + "开始抢第" + ticket + "张票，对象加锁之前：" + System.identityHashCode(ticket));
            synchronized (ticket) {
                System.out.println(Thread.currentThread().getName() + "抢到第" + ticket + "张票，成功锁到的对象：" + System.identityHashCode(ticket));
                if (ticket > 0) {
                    try {
                        //模拟抢票延迟
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "抢到了第" + ticket-- + "张票，票数减一");
                } else {
                    return;
                }
            }
        }
    }
}