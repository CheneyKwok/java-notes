package juc.code.cas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AtomicArrayDemo {

    public static void main(String[] args) {
        // 不安全数组
        demo(
                () -> new int[10],
                array -> array.length,
                (array, index) -> array[index] ++,
                array -> System.out.println(Arrays.toString(array))
        );
        // 安全数组
        demo(
                () -> new AtomicIntegerArray(10),
                AtomicIntegerArray::length,
                AtomicIntegerArray::getAndIncrement,
                array -> System.out.println(array.toString())
        );
    }

    /**
     * Supplier 供给型 () -> T
     * Function 函数型 T -> R
     * Consumer 消费型 T -> void
     *
     * @param arraySupplier 提供数组，可以使线程不安全或安全的数组
     * @param lengthFun 获取数组长度的方法
     * @param putConsumer 自增方法，回传 array, index
     * @param printConsumer 打印数组的方法
     * @param <T> 泛型
     */
    public static <T> void demo(
            Supplier<T> arraySupplier,
            Function<T, Integer> lengthFun,
            BiConsumer<T, Integer> putConsumer,
            Consumer<T> printConsumer
    ) {
        List<Thread> ts = new ArrayList<>();
        T array = arraySupplier.get();
        Integer length = lengthFun.apply(array);
        for (int i = 0; i < length; i++) {
            // 每个线程对数组操作1000次
            ts.add(new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    putConsumer.accept(array, j % length);
                }
            }));
        }
        ts.forEach(Thread::start);
        for (Thread t : ts) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        printConsumer.accept(array);
    }
}
