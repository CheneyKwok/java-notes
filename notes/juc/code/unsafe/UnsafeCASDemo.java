package juc.code.unsafe;

import lombok.Data;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeCASDemo {
    @Data
    static class Student {
        volatile int id;
        volatile String name;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        Unsafe unsafe = UnsafeAccessor.getUnsafe();
        Field id = Student.class.getDeclaredField("id");
        Field name = Student.class.getDeclaredField("name");
        // 获得成员变量的偏移量
        long idOffset = unsafe.objectFieldOffset(id);
        long nameOffset = unsafe.objectFieldOffset(name);
        Student student = new Student();
        // 使用 cas 方法替换成员变量的值
        unsafe.compareAndSwapInt(student, idOffset, 0, 20);
        unsafe.compareAndSwapObject(student, nameOffset, null, "张三");
        System.out.println(student);
    }

}
