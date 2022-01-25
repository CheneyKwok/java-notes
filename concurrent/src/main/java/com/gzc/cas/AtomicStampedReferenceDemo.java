package com.gzc.cas;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicStampedReference;

import static com.gzc.Sleeper.sleep;

@Slf4j
public class AtomicStampedReferenceDemo {

    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);

    public static void main(String[] args) {
        log.info("main start");
        // 获取值 A
        String prev = ref.getReference();
        // 获取版本号
        int stamp = ref.getStamp();
        log.info("版本:{}", stamp);
        other();
        sleep(1);
        log.info("change A -> C {}", ref.compareAndSet(prev, "C", stamp, stamp + 1));
    }

    private static void other() {
        new Thread(() -> {
            String prev = ref.getReference();
            int stamp = ref.getStamp();
            log.info("版本:{}", stamp);
            log.info("change A -> B {}", ref.compareAndSet(prev, "B", stamp, stamp + 1));
        }, "t1").start();

        new Thread(() -> {
            String prev = ref.getReference();
            int stamp = ref.getStamp();
            log.info("版本:{}", stamp);
            log.info("change B -> A {}", ref.compareAndSet(prev, "B", stamp, stamp + 1));
        }, "t2").start();
    }
}
