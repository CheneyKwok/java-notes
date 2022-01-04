package juc.code;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ExecutorsDemo {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            log.info("1");
            int i = 1 / 0;
        });
        executor.execute(() -> log.info("2"));
        executor.execute(() -> log.info("3"));
    }
}
