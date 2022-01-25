package com.gzc.synchronizedkeyword;

import lombok.extern.slf4j.Slf4j;

import static com.gzc.Sleeper.sleep;

@Slf4j
public class SynchronizedLock6 {
    static class Number{
        public synchronized static void a() {
            sleep(1);
            log.info("1");
        }
        public synchronized static void b() {
            log.info("2");
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
    }
}
