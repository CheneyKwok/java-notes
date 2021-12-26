package juc.code.cas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceCASDemo {

    public static void main(String[] args) {

        DecimalAccount.demo(new DecimalAccountCAS(new BigDecimal("1000")));

    }
}

class DecimalAccountCAS implements DecimalAccount {
    AtomicReference<BigDecimal> ref;

    public DecimalAccountCAS(BigDecimal balance) {
        this.ref = new AtomicReference<>(balance);
    }
    @Override
    public BigDecimal getBalance() {
        return ref.get();
    }

    @Override
    public void withdraw(BigDecimal amount) {
        while (true) {
            BigDecimal prev = ref.get();
            BigDecimal next = prev.subtract(amount);
            if (ref.compareAndSet(prev, next)) {
                break;
            }
        }
    }
}

interface DecimalAccount {
    BigDecimal getBalance();

    void withdraw(BigDecimal amount);

    static void demo(DecimalAccount account) {
        List<Thread> ts = new ArrayList<>();

        long start = System.nanoTime();

        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> account.withdraw(BigDecimal.TEN)));

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
        System.out.println(account.getBalance() + " cost: " + (end -start) /10000000 + "ms");
    }
}