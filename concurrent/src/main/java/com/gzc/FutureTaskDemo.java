package com.gzc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Slf4j(topic = "FutureTaskDemo")
public class FutureTaskDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> task = new FutureTask<>(() -> {
            log.debug("running");
            return 100;
        });
        Thread t = new Thread(task, "t");
        t.start();

        log.debug("res: {}", task.get());

    }

}
