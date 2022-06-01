package com.gzc.netty.chatroom.server;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

@Data
@AllArgsConstructor
public class Group {

    /**
     * 聊天室名称
     */
    private String name;

    /**
     * 聊天室成员
     */
    private Set<String> members;

    public static final Group Empty_Group = new Group("empty", Collections.emptySet());
}
