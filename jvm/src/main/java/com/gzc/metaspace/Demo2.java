package com.gzc.metaspace;

import juc.code.Sleeper;

public class Demo2 {

    public static void main(String[] args) {
        String s1 = "a";
        String s2 = "b";
        String s3 = "ab";
        Sleeper.sleep(60);
        String s4 = s1 + s2;
        System.out.println(s3 == s4);
        String s5 = "a" + "b";
        System.out.println(s3 == s5);
    }
}
