package com.gzc.netty.chatroom.server.session;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Set;

/**
 * 聊天组会话管理接口
 */
public interface GroupSession {

    /**
     * 创建一个聊天组
     * @param name 组名
     * @param members 成员
     * @return 成功时返回组对象，否则返回 null
     */
    Group createGroup(String name, Set<String> members);

    /**
     * 加入聊天组
     * @param name 组名
     * @param member 成员
     * @return 成功时返回组对象，否则返回 null
     */
    Group joinMember(String name, String member);

    /**
     * 移除组成员
     * @param name 组名
     * @param member 成员
     * @return 成功时返回组对象，否则返回 null
     */
    Group removeMember(String name, String member);

    /**
     * 移除聊天组
     * @param name 组名
     * @return 成功时返回组对象，否则返回 null
     */
    Group removeGroup(String name);

    /**
     * 获取组成员
     * @param name 组名
     * @return 成功时返回成员集合，否则返回 empty set
     */
    Set<String> getMembers(String name);

    /**
     * 获取组成员的 channel 集合，只有在线的 channel 才会返回
     * @param name 组名
     * @return 成功时返回成员 channel 集合，否则返回 empty list
     */
    List<Channel> getMembersChannel(String name);
}
