package com.gzc.cas;

import java.util.ArrayList;
import java.util.List;

public interface Account {
    Integer getBalance();

    void withdraw(Integer amount);

    static void demo(Account account) {
        List<Thread> ts = new ArrayList<>();

        long start = System.nanoTime();

        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> account.withdraw(10)));

        }
        ts.forEach(Thread::start);
        for (Thread thread : ts) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end = System.nanoTime();
        System.out.println(account.getBalance() + " cost: " + (end - start) / 10000000 + "ms");
    }
}
