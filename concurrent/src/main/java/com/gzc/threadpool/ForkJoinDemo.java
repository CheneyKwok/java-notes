package com.gzc.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinDemo {

    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool(4);
//        System.out.println(pool.invoke(new MyTask(5)));
        System.out.println(pool.invoke(new AddTask(1, 5)));
    }
}

@Slf4j
class MyTask extends RecursiveTask<Integer> {

    int target;

    public MyTask(int target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return target + "";
    }

    @Override
    protected Integer compute() {
        if (target == 1) {
            return target;
        }
        MyTask t1 = new MyTask(target - 1);
        t1.fork();
        int result = t1.join() + target;
        log.info("join() {} + {} = {}", target, t1.join(), result);
        return result;
    }
}

@Slf4j
class AddTask extends RecursiveTask<Integer> {
    int begin;
    int end;

    public AddTask(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if (begin == end) {
            return begin;
        }
        if (end - begin == 1) {
            return end + begin;
        }

        int mid = (begin + end) / 2;
        AddTask t1 = new AddTask(begin, mid);
        AddTask t2 = new AddTask(mid + 1, end);
        t1.fork();
        t2.fork();
        int result = t1.join() + t2.join();
        log.info("join() {} + {} = {}", begin, mid, t1.join());
        log.info("join() {} + {} = {}", mid + 1, end, t2.join());
        log.info("join() {} + {} = {}", t1.join(), t2.join(), result);
        return result;
    }
}