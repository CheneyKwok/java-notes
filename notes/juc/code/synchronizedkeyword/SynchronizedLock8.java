package juc.code.synchronizedkeyword;

import lombok.extern.slf4j.Slf4j;

import static juc.code.Sleeper.sleep;

@Slf4j
public class SynchronizedLock8 {
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
