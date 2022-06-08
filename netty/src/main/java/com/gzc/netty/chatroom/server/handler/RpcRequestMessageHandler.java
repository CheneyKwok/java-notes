package com.gzc.netty.chatroom.server.handler;

import com.gzc.netty.chatroom.message.RpcRequestMessage;
import com.gzc.netty.chatroom.message.RpcResponseMessage;
import com.gzc.netty.chatroom.server.service.ServicesFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) {
        RpcResponseMessage responseMessage = new RpcResponseMessage();
        responseMessage.setSequenceId(msg.getSequenceId());
        try {
            String interfaceName = msg.getInterfaceName();
            Object service = ServicesFactory.getService(Class.forName(interfaceName));
            Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object result = method.invoke(service, msg.getParameterValue());
            responseMessage.setReturnValue(result);
        } catch (Exception e) {
            log.error("rpc invoke failed",e);
            responseMessage.setExceptionValue(new Exception("远程调用出错: " + e.getCause().getMessage()));
        }
        ctx.writeAndFlush(responseMessage);
    }
}
