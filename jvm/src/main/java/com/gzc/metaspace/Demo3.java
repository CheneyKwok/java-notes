package com.gzc.metaspace;

public class Demo3 {

    public static void main(String[] args) {
        String s = new String("a") + new String("b");
        String s1 = s.intern();
        System.out.println(s == "ab");
        System.out.println(s1 == "ab");
    }
}
