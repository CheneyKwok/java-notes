package juc.code.cas;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class AtomicFieldUpdaterDemo {

    private volatile int field;

    public static void main(String[] args) {

        AtomicIntegerFieldUpdater<AtomicFieldUpdaterDemo> updater = AtomicIntegerFieldUpdater.newUpdater(AtomicFieldUpdaterDemo.class, "field");
        AtomicFieldUpdaterDemo demo = new AtomicFieldUpdaterDemo();

        boolean res = updater.compareAndSet(demo, 0, 10);
        System.out.println(res);

        res = updater.compareAndSet(demo, 0, 20);
        System.out.println(res);

    }
}
