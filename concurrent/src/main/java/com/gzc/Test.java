package com.gzc;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Test {

    static boolean stop = false;
    static  int x = 0;

//    public static volatile int race = 0;

    private static final int THREADS_COUNT = 10;

    public static void increase() {
//        race++;
    }


    public static void main(String[] args) {
        Thread updater = new Thread(new Runnable() {
            @Override
            public void run() {
                x = 1;

            }
        }, "updater");

        Thread getter = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (x == 1) {
                        System.out.println("see stop");

                        break;
                    }
                }
//                    }
            }
        }, "getter");
        updater.start();
        getter.start();


//        Thread[] threads = new Thread[THREADS_COUNT];
//        for (int i = 0; i < THREADS_COUNT; i++) {
//            threads[i] = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for (int i = 0; i < 10000; i++) {
//                        increase();
//                    }
//                }
//            });
//            threads[i].start();
//        }
//        while (Thread.activeCount() > 1) {
//
//                ThreadGroup group = Thread.currentThread().getThreadGroup();
//                group.list();
//                Thread.yield();
//        }
//        System.out.println(race);
    }

}

class BlockingQueue<T> {
    private final Deque<T> queue = new ArrayDeque<>();

    private ReentrantLock lock = new ReentrantLock();

    private Condition fullWait = lock.newCondition();

    private Condition emptyWait = lock.newCondition();

    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }


    public void put(T task) {
        try {
            lock.lock();

            while (queue.size() == capacity) {
                try {
                    fullWait.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            queue.addLast(task);
            emptyWait.signal();

        } finally {
            lock.unlock();
        }
    }

    public T take() {
        try {
            lock.lock();
            while (queue.isEmpty()) {
                try {
                    emptyWait.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            T task = queue.removeFirst();
            fullWait.signal();
            return task;

        } finally {
            lock.unlock();

        }
    }

    public T poll(long timeout, TimeUnit timeUnit) {
        try {
            lock.lock();

            long nanos = timeUnit.toNanos(timeout);
            while (queue.isEmpty()) {
                try {
                    if (nanos <= 0) {
                        return null;
                    }
                    nanos = emptyWait.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            T task = queue.removeFirst();
            fullWait.signal();
            return task;
        } finally {
            lock.unlock();
        }
    }

}

@FunctionalInterface
interface RejectPolicy {
    void reject();
}

class ThreadPool {
    private int coreSize;

    private long TimeOut;

    private TimeUnit timeUnit;

    private final BlockingQueue<Runnable> taskQueue;

    private Set<Worker> workers = new HashSet<>();

    private RejectPolicy rejectPolicy;


    ThreadPool(int coreSize, long timeOut, TimeUnit timeUnit, int capacity, RejectPolicy rejectPolicy) {
        this.coreSize = coreSize;
        TimeOut = timeOut;
        this.timeUnit = timeUnit;
        this.rejectPolicy = rejectPolicy;
        this.taskQueue = new BlockingQueue<>(capacity);
    }


    // 执行任务
    public void execute(Runnable task) {
        synchronized (workers) {
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                worker.start();
                workers.add(worker);
            } else {
                taskQueue.put(task);
            }
        }
    }

    class Worker extends Thread {

        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            while (task != null || (task = taskQueue.take()) != null) {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }

            synchronized (this) {
                workers.remove(this);
            }
        }
    }

}
