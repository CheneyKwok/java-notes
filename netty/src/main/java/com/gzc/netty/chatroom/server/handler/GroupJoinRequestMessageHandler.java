package com.gzc.netty.chatroom.server.handler;

import com.gzc.netty.chatroom.message.GroupJoinRequestMessage;
import com.gzc.netty.chatroom.message.GroupJoinResponseMessage;
import com.gzc.netty.chatroom.server.session.Group;
import com.gzc.netty.chatroom.server.session.GroupSession;
import com.gzc.netty.chatroom.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        final GroupSession groupSession = GroupSessionFactory.getGroupSession();
        final Group group = groupSession.joinMember(msg.getGroupName(), msg.getUsername());
        if (group != null) {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, "群加入成功"));
            groupSession
                    .getMembersChannel(group.getName())
                    .stream()
                    .filter(e -> !e.equals(ctx.channel()))
                    .forEach(e -> e.writeAndFlush(new GroupJoinResponseMessage(true, msg.getUsername() + " 加入群")));
        } else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(false, "加入失败"));
        }
    }
}
