package com.gzc.synchronizedkeyword;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynchronizedLock1 {
    static class Number{
        public synchronized void a() {
            log.info("1");
        }
        public synchronized void b() {
            log.info("2");
        }
    }

    public static void main(String[] args) {
        Number number = new Number();
        new Thread(number::a).start();
        new Thread(number::b).start();
    }
}
