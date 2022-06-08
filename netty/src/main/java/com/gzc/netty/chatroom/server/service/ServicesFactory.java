package com.gzc.netty.chatroom.server.service;

import com.gzc.netty.chatroom.config.Config;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServicesFactory {

    static Map<Class<?>, Object> map = new ConcurrentHashMap<>();

    static {
        try {
            Properties properties = Config.getProperties();
            Set<String> names = properties.stringPropertyNames();
            for (String name : names) {

                if (name.endsWith("Service")) {
                    Class<?> interfaceClass = Class.forName(name);
                    Class<?> instanceClass = Class.forName(properties.getProperty(name));
                    map.put(interfaceClass, instanceClass.newInstance());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getService(Class<T> clazz) {
        return clazz.cast(map.get(clazz));
    }
}
