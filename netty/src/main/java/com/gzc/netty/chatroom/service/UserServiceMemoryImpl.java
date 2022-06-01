package com.gzc.netty.chatroom.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceMemoryImpl implements UserService {

    private Map<String, String> allUserMap = new ConcurrentHashMap<String, String>() {
        {
            put("zhangsan", "123");
            put("lisi", "123");
            put("wangwu", "123");
            put("zhaoliu", "123");
            put("qianqi", "123");
        }
    };


    @Override
    public boolean login(String username, String password) {
        final String pass = allUserMap.get(username);
        if (pass == null) {
            return false;
        }
        return pass.equals(password);
    }
}
