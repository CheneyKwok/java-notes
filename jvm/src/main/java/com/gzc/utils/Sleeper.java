package com.gzc.utils;

import java.util.concurrent.TimeUnit;

public class Sleeper {

    public static void sleep(int i) {
        try {
            TimeUnit.SECONDS.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(double i) {
        try {
            TimeUnit.MILLISECONDS.sleep((int)i* 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
