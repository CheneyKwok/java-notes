package com.gzc.aqs;

import com.gzc.Sleeper;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Slf4j
public class AQSDemo {

    public static void main(String[] args) {
        MyLock lock = new MyLock();
        new Thread(() -> {
            lock.lock();
            log.info("locking.....");
            lock.lock();
            log.info("locking.....");
            try {
                log.info("lock....");
                Sleeper.sleep(1);
            } finally {
                lock.unlock();
                log.info("unlock....");
            }
        }, "t1").start();

        new Thread(() -> {
            lock.lock();
            try {
                log.info("lock....");
            } finally {
                lock.unlock();
                log.info("unlock....");
            }
        }, "t2").start();
    }
}

// 自定义锁（不可重入锁）
class MyLock implements Lock {

    // 独占锁，同步器类
    class MySync extends AbstractQueuedSynchronizer {

        @Override
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            // 注意 exclusiveOwnerThread 无 volatile 修饰，而 state 有 volatile 修饰，会在 setState() 后加写屏障
            // 所以setExclusiveOwnerThread() 需放在 setState() 方法之前保证一个线程对exclusiveOwnerThread的修改对其他所有线程可见
            setState(0);
            return true;
        }

        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        protected Condition newCondition() {
            return new ConditionObject();
        }
    }

    private MySync sync = new MySync();

    // 尝试，不成功，进入等待队列
    @Override
    public void lock() {
        sync.acquire(1);
    }

    // 尝试，不成功，进入等待队列，可打断
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);

    }

    // 尝试一次，不成功返回，不进入等待队列
    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    // 尝试一次，不成功返回，不进入等待队列，有时限
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    // 释放锁
    @Override
    public void unlock() {
        sync.release(0);
    }

    // 生成条件变量
    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}