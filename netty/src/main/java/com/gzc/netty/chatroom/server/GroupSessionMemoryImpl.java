package com.gzc.netty.chatroom.server;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GroupSessionMemoryImpl implements GroupSession{

    private final Map<String, Group> groupMap = new ConcurrentHashMap<>();

    @Override
    public Group createGroup(String name, Set<String> members) {
        return null;
    }

    @Override
    public Group joinMember(String name, String member) {
        return null;
    }

    @Override
    public Group removeMember(String name, String member) {
        return null;
    }

    @Override
    public Group removeGroup(String name) {
        return null;
    }

    @Override
    public Set<String> getMembers(String name) {
        return null;
    }

    @Override
    public List<Channel> getMembersChannel(String name) {
        return null;
    }
}
