package juc.code;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

@Slf4j
public class SyncModeDemo2 {

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
                log.info("1");
        }, "t1");

        Thread t2 = new Thread(() -> {
            LockSupport.unpark(t1);
            log.info("2");
        }, "t2");

        t1.start();
        t2.start();
    }
}
