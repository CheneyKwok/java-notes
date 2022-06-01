package com.gzc.netty.chatroom.server;

import io.netty.channel.Channel;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GroupSessionMemoryImpl implements GroupSession{

    /**
     * key: 组名 name
     * value: 组 group
     */
    private final Map<String, Group> groupMap = new ConcurrentHashMap<>();

    @Override
    public Group createGroup(String name, Set<String> members) {
        Group group = new Group(name, members);
        return groupMap.putIfAbsent(name, group);
    }

    @Override
    public Group joinMember(String name, String member) {
        return groupMap.computeIfPresent(name, (k, group) -> {
            group.getMembers().add(member);
            return group;
        });
    }

    @Override
    public Group removeMember(String name, String member) {
        return groupMap.computeIfPresent(name, (k, group) -> {
            group.getMembers().remove(member);
            return group;
        });
    }

    @Override
    public Group removeGroup(String name) {
        return groupMap.remove(name);
    }

    @Override
    public Set<String> getMembers(String name) {
        return groupMap.getOrDefault(name, Group.Empty_Group).getMembers();
    }

    @Override
    public List<Channel> getMembersChannel(String name) {
        return null;
    }
}
