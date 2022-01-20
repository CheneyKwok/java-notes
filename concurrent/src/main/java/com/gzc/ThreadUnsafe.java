package com.gzc;

import java.util.ArrayList;
import java.util.List;

public class ThreadUnsafe {
    List<String> list = new ArrayList<>();
    public void method1(int loopNumber) {
        for (int i = 0; i < loopNumber; i++) {
            // 临界区 会产生竟态条件
            method2();
            method3();
        }
    }

    private void method2() {
        list.add("1");
    }

    private void method3() {
        list.remove(0);
    }

    public static void main(String[] args) {
        ThreadUnsafe tu = new ThreadUnsafe();
        for (int i = 0; i < 5; i++) {
            new Thread(() -> tu.method1(200), "Thread" + i).start();
        }
    }
}
