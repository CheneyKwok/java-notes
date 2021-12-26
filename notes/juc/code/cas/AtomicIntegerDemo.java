package juc.code.cas;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerDemo {

    public static void main(String[] args) {

        AtomicInteger i = new AtomicInteger();
        // 获取并自增 类似 i++
        System.out.println(i.getAndIncrement());
        // 自增并获取 类似 ++i
        System.out.println(i.incrementAndGet());
        // 自减并获取 类似 --i
        System.out.println(i.decrementAndGet());
        // 获取并自减 类似 i--
        System.out.println(i.getAndDecrement());
        // 获取并加值
        System.out.println(i.getAndAdd(5));
        // 加值并获取
        System.out.println(i.addAndGet(-5));
        // 获取并更新
        // 其中函数中的操作能保证原子性，但函数需要无副作用
        System.out.println(i.getAndUpdate(p -> p - 2));
        // 更新并获取
        System.out.println(i.updateAndGet(p -> p + 2));
        // 获取并计算
        // p = i, x = 10
        // 其中函数中的操作能保证原子性，但函数需要无副作用
        // getAndUpdate 如果在 lambda 中引用了外部的局部变量，要保证变量是 final
        // getAndAccumulate 可以通过参数 x 来引用外部的局部变量，且因其不在 lambda 中故不必是 final
        System.out.println(i.getAndAccumulate(10, (p, x) -> p + x));
        // 计算并获取
        System.out.println(i.accumulateAndGet(-10, (p, x) -> p + x));

    }
}
