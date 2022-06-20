package com.gzc.gc;

import java.util.ArrayList;

public class Demo1 {
    private static final int _512KB = 512 * 1024;
    private static final int _1MB = 1024 * 1024;
    private static final int _6MB = 6 * _1MB;
    private static final int _7MB = 7 * _1MB;
    private static final int _8MB = 8 * _1MB;

    // -Xms20M -Xmx20M -Xmn10M -XX:+UseSerialGC -XX:+PrintGCDetails -verbose:gc
    // XX:+UseSerialGC 使用指定的GC，不会去动态调整幸存区的大小
    public static void main(String[] args) throws InterruptedException {
        ArrayList<byte[]> list = new ArrayList<>();
//        list.add(new byte[_7MB]);
//        list.add(new byte[_512KB]);
//        list.add(new byte[_512KB]);
//        list.add(new byte[_8MB]);

        new Thread(() -> {
            list.add(new byte[_8MB]);
            list.add(new byte[_8MB]);
        }).start();

        System.out.println("sleep..........");
        Thread.sleep(1000000000L);
    }
}
