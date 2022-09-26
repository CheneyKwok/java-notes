package github.cheneykwok.spring.web.a23;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;

import java.util.Date;

public class TestServletDataBinder {
    public static void main(String[] args) {
        // web 环境下绑定
        MyBean bean = new MyBean();
        ServletRequestDataBinder binder = new ServletRequestDataBinder(bean);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("a", "10");
        request.addParameter("b", "hello");
        request.addParameter("c", "1999/01/01");
        binder.bind(request);
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
