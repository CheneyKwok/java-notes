package com.gzc.netty.chatroom.server.handler;

import com.gzc.netty.chatroom.message.GroupQuitRequestMessage;
import com.gzc.netty.chatroom.message.GroupQuitResponseMessage;
import com.gzc.netty.chatroom.server.session.Group;
import com.gzc.netty.chatroom.server.session.GroupSession;
import com.gzc.netty.chatroom.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        final GroupSession groupSession = GroupSessionFactory.getGroupSession();
        final Group group = groupSession.removeMember(msg.getGroupName(), msg.getUsername());
        if (group != null) {
            ctx.writeAndFlush(new GroupQuitResponseMessage(true, "群退出成功"));
            groupSession
                    .getMembersChannel(group.getName())
                    .stream()
                    .filter(e -> !e.equals(ctx.channel()))
                    .forEach(e -> e.writeAndFlush(new GroupQuitResponseMessage(true, msg.getUsername() + " 已退出群")));
        } else {
            ctx.writeAndFlush(new GroupQuitResponseMessage(false, "退出失败"));
        }
    }
}
