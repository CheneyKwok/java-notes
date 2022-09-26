package github.cheneykwok.spring.web.a23;

import org.springframework.beans.BeanWrapperImpl;

import java.util.Date;

public class TestBeanWrapper {

    public static void main(String[] args) {
        MyBean bean = new MyBean();
        // 利用反射原理, 为 bean 的属性赋值，利用字段的 get、set 方法绑定
        BeanWrapperImpl wrapper = new BeanWrapperImpl(bean);
        wrapper.setPropertyValue("a", "10");
        wrapper.setPropertyValue("b", "hello");
        wrapper.setPropertyValue("c", "1999/01/01");
        System.out.println(bean);

    }

    static class MyBean {
        private int a;
        private String b;
        private Date c;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public Date getC() {
            return c;
        }

        public void setC(Date c) {
            this.c = c;
        }

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
