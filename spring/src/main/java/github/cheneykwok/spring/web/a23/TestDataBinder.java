package github.cheneykwok.spring.web.a23;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;

import java.util.Date;

public class TestDataBinder {

    public static void main(String[] args) {
        MyBean bean = new MyBean();
        DataBinder binder = new DataBinder(bean);
        MutablePropertyValues values = new MutablePropertyValues();
        values.add("a", "10");
        values.add("b", "str");
        values.add("c", "1999/01/01");
        // 直接通过字段绑定
        binder.initDirectFieldAccess();
        binder.bind(values);
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
