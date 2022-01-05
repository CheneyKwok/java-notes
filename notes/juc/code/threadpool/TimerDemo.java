package juc.code.threadpool;

import juc.code.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class TimerDemo {

    public static void main(String[] args) {
        Timer timer = new Timer();
        log.info("start....");
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                log.info("run task1....");
                Sleeper.sleep(2);
            }
        };
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                log.info("run task2....");
            }
        };
        timer.schedule(task1, 1000);
        timer.schedule(task2, 1000);
    }
}
