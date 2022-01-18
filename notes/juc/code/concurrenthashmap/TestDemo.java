package juc.code.concurrenthashmap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestDemo {

    public static void main(String[] args) throws InterruptedException {
        Map<Object, Object> map = new ConcurrentHashMap<>();
        System.out.println(map.size());
        Thread t1 = new Thread(() -> map.put(1, 1), "guo-1");
        Thread t2 = new Thread(() -> map.put(1, 1), "guo-2");
        t1.start();
        t2.start();
        System.out.println(map.size());
    }
}
