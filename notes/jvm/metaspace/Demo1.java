package jvm.metaspace;

import com.sun.xml.internal.ws.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * 演示元空间内存溢出 java.lang.OutOfMemoryError: Metaspace
 * -XX:MaxMetaspaceSize=8m
 */
public class Demo1 extends ClassLoader {

    public static void main(String[] args) {
        int j = 0;
        try {
            Demo1 demo1 = new Demo1();
            for (int i = 0; i < 10000; i++, j++) {
                // 作用是生成类的二进制字节码
                ClassWriter cw = new ClassWriter(0);
                // 版本号、访问修饰符、类名、包名、父类、接口
                cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "Class" + i, null, "java/lang/Object", null);
                // 返回 byte[]
                byte[] code = cw.toByteArray();
                // 执行类的加载
                demo1.defineClass("Class" + i, code, 0, code.length);
            }

        }finally {
            System.out.println(j);
        }
    }
}
