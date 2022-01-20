package com.gzc.cas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicMarkableReference;

import static juc.code.Sleeper.sleep;

@Slf4j
public class AtomicMarkableReferenceDemo {

    public static void main(String[] args) {
        GarbageBag bag = new GarbageBag("装满了垃圾");
        AtomicMarkableReference<GarbageBag> ref = new AtomicMarkableReference<>(bag, true);
        log.info("start....");
        GarbageBag prev = ref.getReference();
        log.info(prev.toString());

        new Thread(() -> {
            log.info("other start ...");
            bag.setDesc("空垃圾袋");
            ref.compareAndSet(bag, bag, true, false);
        }).start();

        sleep(1);
        log.info("想换一个新垃圾袋");
        boolean success = ref.compareAndSet(prev, new GarbageBag("空垃圾袋"), true, false);
        log.info("结果：{}", success);
        log.info(ref.getReference().toString());

    }
}

@Getter
@Setter
@ToString
@AllArgsConstructor
class GarbageBag {
    String desc;
}
