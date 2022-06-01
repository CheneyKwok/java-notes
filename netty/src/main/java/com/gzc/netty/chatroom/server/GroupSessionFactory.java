package com.gzc.netty.chatroom.server;

public class GroupSessionFactory {

    private static GroupSession session = new GroupSessionMemoryImpl();

    private static GroupSession getGroupSession() {
        return session;
    }
}
