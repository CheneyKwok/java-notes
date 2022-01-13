package juc.code.reentrantlock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CachedData {
    Object data;
    // 是否有效，如果失效，需要重新计算 data
    volatile boolean cacheValid;
    final ReentrantReadWriteLock r = new ReentrantReadWriteLock();

    void processCachedData() {
        r.readLock().lock();
        if (!cacheValid) {
            // 读取写锁前必须释放读锁
            r.readLock().unlock();
            r.writeLock().lock();
            try {
                // 判断是否有其他线程已经获取了写锁、更新了缓存，避免重复更新
                if (!cacheValid) {
                    // data =...
                    cacheValid = true;
                }
                // 降级为读锁，释放写锁，这样能够让其他线程读取缓存
                r.readLock().lock();
            } finally {
                r.writeLock().unlock();
            }
        }
        // 自己用完数据，释放读锁
        try {
            // use(data)
        }finally {
            r.readLock().unlock();
        }
    }
}
