package com.gzc.heap;

import java.util.ArrayList;
import java.util.List;

/**
 * 演示堆内存溢出
 * -Xmx8m
 */
public class Demo1 {

    public static void main(String[] args) {
        int i = 0;
        try {
            List<String> list = new ArrayList<>();
            String a = "hello";
            while (true) {
                list.add(a);
                a = a + a;
                i++;
            }
        } catch (Throwable e) {
            System.out.println(i);
            e.printStackTrace();
        }
    }
}
