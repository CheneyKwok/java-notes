package juc.code.synchronizedkeyword;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynchronizedDemo {

    static int counter = 0;
    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
                for (int i = 0; i < 5000000; i++) {
                    synchronized (lock){
                        log.info("{} ---- {}", Thread.currentThread().getName(), counter);

                        counter++;
                }
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
                for (int i = 0; i < 5000000; i++) {
                    synchronized (lock) {
                        log.info("{} ---- {}", Thread.currentThread().getName(), counter);
                        counter--;
                }
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.info("{}", counter);
    }
}
