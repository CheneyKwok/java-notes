package com.gzc.synchronizedkeyword;

import lombok.extern.slf4j.Slf4j;

import static com.gzc.Sleeper.sleep;

@Slf4j
public class SynchronizedLock7 {
    static class Number{
        public synchronized static void a() {
            sleep(1);
            log.info("1");
        }
        public synchronized void b() {
            log.info("2");
        }
    }

    public static void main(String[] args) {
        Number n1 = new Number();
        Number n2 = new Number();
        new Thread(() ->{
            log.info("begin");
            n1.a();
        }).start();
        new Thread(() ->{
            log.info("begin");
            n2.b();
        }).start();
    }
}
