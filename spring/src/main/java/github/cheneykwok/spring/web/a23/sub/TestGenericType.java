package github.cheneykwok.spring.web.a23.sub;

import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TestGenericType {
    public static void main(String[] args) {
        // 获取泛型参数
        // 1. java api
        Type type = TeacherDao.class.getGenericSuperclass();
        if (type instanceof ParameterizedType ) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            type = parameterizedType.getActualTypeArguments()[0];
            System.out.println(type);
        }
         // 2. spring api 1
        Class<?> t = GenericTypeResolver.resolveTypeArgument(TeacherDao.class, BaseDao.class);
        System.out.println(t);
    }

}
