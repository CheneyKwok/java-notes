package com.gzc.waitnotify;

import lombok.extern.slf4j.Slf4j;

import static juc.code.Sleeper.sleep;


@Slf4j
public class WaitNotifyDemo1 {

    final static Object obj = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (obj){
                log.info("执行...");
                try {
                    obj.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                log.info("其他代码...");
            }
        }, "t1").start();

        new Thread(() -> {
            synchronized (obj){
                log.info("执行...");
                try {
                    obj.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                log.info("其他代码...");
            }
        }, "t2").start();

        sleep(2);
        log.info("唤醒 obj 上其他线程");
        synchronized (obj) {
            // 唤醒 obj 上一个线程
//            obj.notify();
            // 唤醒 obj 上全部线程
            obj.notifyAll();
        }
    }
}
