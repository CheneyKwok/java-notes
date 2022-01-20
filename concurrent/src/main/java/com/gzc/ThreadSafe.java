package com.gzc;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.gzc.Sleeper.sleep;

@Slf4j
public class ThreadSafe {
    public final void method1(int loopNumber) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < loopNumber; i++) {
            // 临界区 会产生竟态条件
            method2(list);
            method3(list);
            log.info("list size:{}", list.size());
        }
    }

    public void method2(List<String> list) {
        list.add("1");
    }

    public void method3(List<String> list) {
        list.remove(0);
    }

    public static void main(String[] args) {
        ThreadSafeSub tu = new ThreadSafeSub();
        for (int i = 0; i < 3; i++) {
            new Thread(() -> tu.method1(200), "Thread" + i).start();
        }
    }
}

class ThreadSafeSub extends ThreadSafe {

    @Override
    public void method3(List<String> list) {
        new Thread(() -> list.remove(0)).start();
    }
}
