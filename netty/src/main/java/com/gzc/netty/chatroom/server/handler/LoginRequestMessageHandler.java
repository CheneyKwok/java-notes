package com.gzc.netty.chatroom.server.handler;

import com.gzc.netty.chatroom.message.LoginRequestMessage;
import com.gzc.netty.chatroom.message.LoginResponseMessage;
import com.gzc.netty.chatroom.server.service.UserService;
import com.gzc.netty.chatroom.server.service.UserServiceFactory;
import com.gzc.netty.chatroom.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        UserService userService = UserServiceFactory.getUserService();
        boolean login = userService.login(username, password);
        LoginResponseMessage responseMessage;
        if (login) {
            SessionFactory.getSession().bind(ctx.channel(), username);
            responseMessage = new LoginResponseMessage(true, "登录成功");
        } else {
            responseMessage = new LoginResponseMessage(false, "用户名或密码不正确");
        }
        ctx.writeAndFlush(responseMessage);
    }
}
