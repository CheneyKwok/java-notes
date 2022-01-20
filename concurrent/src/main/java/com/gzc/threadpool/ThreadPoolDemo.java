package com.gzc.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ThreadPoolDemo{
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(2, 1, TimeUnit.SECONDS, 5, ((queue, task) -> {
            // 1) 死等
//            queue.put(task);
            // 2) 带超时等待
//            queue.offer(task, 1500, TimeUnit.MILLISECONDS);
            // 3) 让调用者放弃任务
//            log.info("放弃 {}", task);
            // 4) 让调用者抛出异常
//            throw new RuntimeException("任务执行失败 " + task);
            // 5) 让调用者自己执行任务
            task.run();
        }));
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            threadPool.execute(() -> log.info("{}", finalI));
        }
    }
}

/** 拒绝策略 */
@FunctionalInterface
interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue, T task);
}

@Slf4j
class ThreadPool {
    /** 任务队列 */
    private final BlockingQueue<Runnable> taskQueue ;

    /** 线程集合 */
    private final HashSet<Worker> workers = new HashSet<>();

    /** 核心线程数*/
    private int coreSize;

    /** 获取任务的超时时间 */
    private long timeOut;

    private TimeUnit unit;

    /** 拒绝策略 */
    private final RejectPolicy<Runnable> rejectPolicy;

    public ThreadPool(int coreSize, long timeOut, TimeUnit unit, int queueCapacity, RejectPolicy<Runnable> rejectPolicy) {
        this.taskQueue = new BlockingQueue<>(queueCapacity);
        this.coreSize = coreSize;
        this.timeOut = timeOut;
        this.unit = unit;
        this.rejectPolicy = rejectPolicy;
    }

    // 执行任务
    public void execute(Runnable task) {
        synchronized (workers) {
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.info("新建线程:{}", worker);
                worker.start();
                workers.add(worker);
            } else {
                log.info("加入队列：{}", task);
//                taskQueue.put(task);
                // 1) 死等
                // 2) 带超时等待
                // 3) 让调用者放弃任务
                // 4) 让调用者抛出异常
                // 5) 让调用者自己执行任务
                taskQueue.tryPut(rejectPolicy, task);
            }
        }

    }

    class Worker extends Thread {

        private Runnable task;
        public Worker() {

        }

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
//            while (task != null || (task = taskQueue.take()) != null) {
            while (task != null || (task = taskQueue.poll(timeOut, unit)) != null) {
                // 有任务则执行
                try {
                    log.info("正在执行....{}", task);
                    task.run();
                } catch (Exception e) {
                    e.getStackTrace();
                } finally {
                    task = null;
                }
            }

            synchronized (workers) {
                workers.remove(this);
                log.info("worker 被移除 {}", this);
            }
        }
    }
}

class BlockingQueue<T> {

    /** 任务队列 */
    private final Deque<T> queue = new ArrayDeque<>();

    /** 锁 */
    private ReentrantLock lock = new ReentrantLock();

    /** 生产者条件变量 */
    private Condition fullWaitSet = lock.newCondition();

    /** 消费者条件变量 */
    private Condition emptyWaitSet = lock.newCondition();

    /** 容量 */
    private final int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    /** 阻塞获取 */
    public T take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }

    }

    /** 带超时的阻塞获取 */
    public T poll(long timeOut, TimeUnit unit) {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeOut);
            while (queue.isEmpty()) {
                try {
                    if (nanos <= 0) return null;
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        }finally {
            lock.unlock();
        }
    }

    /** 阻塞添加 */
    public void put(T task) {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                try {
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(task);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }

    }

    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {

        lock.lock();
        try {
            if (queue.size() == capacity) {
                rejectPolicy.reject(this, task);
            } else {
                queue.addLast(task);
                emptyWaitSet.signal();
            }

        }finally {
            lock.unlock();
        }

    }

    /** 带超时时间的添加 */
    public boolean offer(T element, long timeOut, TimeUnit unit) {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeOut);
            while (queue.size() == capacity) {
                try {
                    if (nanos <= 0) {
                        return false;
                    }
                    nanos = fullWaitSet.awaitNanos(nanos);
                    return false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(element);
            emptyWaitSet.signal();
            return true;
        }finally {
            lock.unlock();
        }
    }

    /** 获取容量 */
    public int size() {
        lock.lock();
        try {
            return queue.size();
        }finally {
            lock.unlock();
        }
    }


}
