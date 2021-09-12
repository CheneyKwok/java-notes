package juc.code;

import lombok.extern.slf4j.Slf4j;

import static juc.code.Sleeper.sleep;


/**
 * 案例：烧水泡茶
 */
@Slf4j(topic = "Case1")
public class Case1 {

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.info("洗水壶");
            sleep(1);
            log.info("烧开水");
            sleep(5);
        }, "t1");

        Thread t2 = new Thread(() -> {
            log.info("洗茶壶");
            sleep(1);
            log.info("洗茶杯");
            sleep(2);
            log.info("拿茶叶");
            sleep(1);
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("泡茶");
        }, "t2");
        t1.start();
        t2.start();
    }
}
