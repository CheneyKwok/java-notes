package juc.code.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class WorkerThreadDemo {

    static final List<String> MENU = Arrays.asList("地三鲜", "宫保鸡丁", "辣子鸡丁");
    static Random RANDOM = new Random();
    static String cooking() {
        return MENU.get(RANDOM.nextInt(MENU.size()));
    }

    public static void main(String[] args) {
        ExecutorService cookPool = Executors.newFixedThreadPool(1);
        ExecutorService waiterPool = Executors.newFixedThreadPool(1);

        waiterPool.execute(() -> {
            log.info("处理点餐...");
            Future<String> future = cookPool.submit(() -> {
                log.info("做菜");
                return cooking();
            });
            try {
                log.info("上菜...{}", future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        waiterPool.execute(() -> {
            log.info("处理点餐...");
            Future<String> future = cookPool.submit(() -> {
                log.info("做菜");
                return cooking();
            });
            try {
                log.info("上菜...{}", future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
