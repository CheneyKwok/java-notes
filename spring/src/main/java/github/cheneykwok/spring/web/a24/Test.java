package github.cheneykwok.spring.web.a24;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class Test {

    public static void main(String[] args) throws Exception {
//        String s = new String("abc");
//        Field value = s.getClass().getDeclaredField("value");
//        value.setAccessible(true);
//        value.set(s, "abcd".toCharArray());
//        System.out.println(s);

        Integer a = 127;
        Integer b = new Integer(127);
        System.out.println(a == b);


    }
}
