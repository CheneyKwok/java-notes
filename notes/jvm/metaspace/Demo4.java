package jvm.metaspace;

public class Demo4 {

    public static void main(String[] args) {
        // 堆 new String("a") new String("b") 再由 StringBuilder 拼接为 new String("ab")
        String s = new String("a") + new String("b");
        // 而 "ab" 是常量池中的符号，运行时在串池中，s 指向堆中 ab 对象的引用，二者地址不等
        System.out.println(s == "ab");
        String s2 = s.intern();
        System.out.println(s == "ab");
        System.out.println(s2 == "ab");

    }
}
