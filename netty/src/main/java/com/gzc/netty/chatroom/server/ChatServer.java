package com.gzc.netty.chatroom.server;

import com.gzc.netty.chatroom.protocol.MessageCodecSharable;
import com.gzc.netty.chatroom.protocol.ProtocolFrameDecoder;
import com.gzc.netty.chatroom.server.handler.ChatRequestMessageHandler;
import com.gzc.netty.chatroom.server.handler.GroupCreateRequestMessageHandler;
import com.gzc.netty.chatroom.server.handler.LoginRequestMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        LoginRequestMessageHandler loginHandler = new LoginRequestMessageHandler();
        ChatRequestMessageHandler chatHandler = new ChatRequestMessageHandler();
        GroupCreateRequestMessageHandler groupCreateHandler = new GroupCreateRequestMessageHandler();
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ProtocolFrameDecoder())
                                    .addLast(messageCodec)
//                                    .addLast(loggingHandler)
                                    .addLast(loginHandler)
                                    .addLast(chatHandler)
                                    .addLast(groupCreateHandler);
                        }
                    })
                    .bind(8888)
                    .sync();
            log.info("server start success [{}]", channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
