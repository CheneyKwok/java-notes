package com.gzc.heap;

import com.gzc.Sleeper;

/**
 * 演示堆内存
 */
public class Demo2 {

    public static void main(String[] args) {
        System.out.println("1......");
        Sleeper.sleep(30);
        byte[] array = new byte[1024 * 1024 * 10]; // 10M
        System.out.println("2....");
        Sleeper.sleep(30);
        array = null;
        System.gc();
        System.out.println("3.......");
        Sleeper.sleep(1000);
    }
}
