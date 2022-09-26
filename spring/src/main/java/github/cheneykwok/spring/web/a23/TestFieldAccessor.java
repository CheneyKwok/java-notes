package github.cheneykwok.spring.web.a23;

import org.springframework.beans.DirectFieldAccessor;

import java.util.Date;

public class TestFieldAccessor {

    public static void main(String[] args) {
        MyBean bean = new MyBean();
        DirectFieldAccessor accessor = new DirectFieldAccessor(bean);
        accessor.setPropertyValue("a", "10");
        accessor.setPropertyValue("b", "str");
        accessor.setPropertyValue("c", "1999/01/01");
        System.out.println(bean);
    }

    static class MyBean {
        private int a;
        private String b;
        private Date c;

        @Override
        public String toString() {
            return "MyBean{" +
                    "a=" + a +
                    ", b='" + b + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
}
