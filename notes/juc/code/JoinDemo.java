package juc.code;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class JoinDemo {
    static int r = 0;

    public static void main(String[] args) {
        test1();
    }

    private static void test1() {
        log.debug("start");
        Thread t1 = new Thread(() -> {
            log.debug("start");
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            log.debug("end");
            r = 10;
        },"t1");

        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("res: {}", r);
        log.debug("end");
    }
}
