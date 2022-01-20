package com.gzc.synchronizedkeyword;

import lombok.extern.slf4j.Slf4j;

import static juc.code.Sleeper.sleep;

@Slf4j
public class SynchronizedLock3 {
    static class Number{
        public synchronized void a() {
            sleep(1);
            log.info("1");
        }
        public synchronized void b() {
            log.info("2");
        }

        public void c() {
            log.info("3");
        }
    }

    public static void main(String[] args) {
        Number number = new Number();
        new Thread(() ->{
            log.info("begin");
            number.a();
        }).start();
        new Thread(() ->{
            log.info("begin");
            number.b();
        }).start();
        new Thread(() ->{
            log.info("begin");
            number.c();
        }).start();
    }
}
