package com.gzc;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class GuardedDemo {

    public static void main(String[] args) {
        GuardedObject guarded = new GuardedObject();
        new Thread(() ->{
            log.info("等待结果");
            List<String> list = (List<String>) guarded.get();
            log.info("结果大小：{}", list.size());
        },"t1").start();

        new Thread(() -> {
            log.info("执行下载");
            try {
                List<String> strings = Downloader.downLoad();
                guarded.complete(strings);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }
}
class GuardedObject{
    // 结果
    private Object response;
    public Object get() {
        synchronized (this) {
            while (response == null){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }
    public void complete(Object obj) {
        synchronized (this){
            this.response = obj;
            this.notifyAll();
        }
    }
}
