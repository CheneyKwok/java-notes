package com.gzc.netty.chatroom.server;

public abstract class SessionFactory {

    private static Session session = new SessionMemoryImpl();

    private static Session getSession() {
        return session;
    }
}
