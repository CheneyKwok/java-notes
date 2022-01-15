package juc.code.reentrantlock;

import juc.code.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class ReentrantReadWriteLockDemo {

    public static void main(String[] args) {
        DataContainer container = new DataContainer(0);
        // 读-写
//        new Thread(container::read, "t1").start();
//        new Thread(() -> container.write(1), "t2").start();

        // 读-读
//        new Thread(container::read, "t1").start();
//        new Thread(container::read, "t2").start();

        // 写-写
        new Thread(() -> container.write(1), "t1").start();
        new Thread(() -> container.write(2), "t2").start();

    }

}

@Slf4j
class DataContainer {
    private Object data;
    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock r = rw.readLock();
    private ReentrantReadWriteLock.WriteLock w = rw.writeLock();

    public DataContainer(Object data) {
        this.data = data;
    }

    public Object read() {
        log.info("获取读锁...");
        r.lock();
        try {
            log.info("读取");
            Sleeper.sleep(1);
            return data;
        } finally {
            log.info("释放读锁...");
            r.unlock();
        }
    }

    public void write(Object data) {
        log.info("获取写锁");
        w.lock();
        try {
            log.info("写入");
            this.data = data;
            Sleeper.sleep(1);
        } finally {
            log.info("释放写锁...");
            w.unlock();
        }

    }
}
