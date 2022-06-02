package com.gzc.netty.chatroom.server.handler;


import com.gzc.netty.chatroom.message.GroupCreateRequestMessage;
import com.gzc.netty.chatroom.message.GroupCreateResponseMessage;
import com.gzc.netty.chatroom.server.session.Group;
import com.gzc.netty.chatroom.server.session.GroupSession;
import com.gzc.netty.chatroom.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, msg.getMembers());
        if (group == null) {
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, groupName + "创建成功"));
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            for (Channel channel : channels) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "你已被拉入群 " + groupName));
            }
        } else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, groupName + "已存在"));
        }
    }
}
