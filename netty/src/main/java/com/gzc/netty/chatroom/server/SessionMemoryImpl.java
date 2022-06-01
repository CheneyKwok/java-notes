package com.gzc.netty.chatroom.server;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionMemoryImpl implements Session {

    private final Map<String, Channel> usernameChannelMap = new ConcurrentHashMap<>();
    private final Map<Channel, String> channelUsernameMap = new ConcurrentHashMap<>();
    private final Map<Channel, Map<String, Object>> channelAttributeMap = new ConcurrentHashMap<>();

    @Override
    public void bind(Channel channel, String username) {
        usernameChannelMap.put(username, channel);
        channelUsernameMap.put(channel, username);
        channelAttributeMap.put(channel, new ConcurrentHashMap<>());
    }

    @Override
    public void unbind(Channel channel) {
        String username = channelUsernameMap.remove(channel);
        usernameChannelMap.remove(username);
        channelAttributeMap.remove(channel);
    }

    @Override
    public Object getAttribute(Channel channel, String name) {
        return channelAttributeMap.get(channel).get(name);
    }

    @Override
    public void setAttribute(Channel channel, String name, Object value) {
        channelAttributeMap.get(channel).put(name, value);
    }

    @Override
    public Channel getChannel(String username) {
        return usernameChannelMap.get(username);
    }

    @Override
    public String toString() {
        return usernameChannelMap.toString();
    }
}
