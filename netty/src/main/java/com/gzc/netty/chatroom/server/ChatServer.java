package com.gzc.netty.chatroom.server;

import com.gzc.netty.chatroom.config.Config;
import com.gzc.netty.chatroom.protocol.MessageCodecSharable;
import com.gzc.netty.chatroom.protocol.ProtocolFrameDecoder;
import com.gzc.netty.chatroom.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
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
        final GroupJoinRequestMessageHandler groupJoinHandler = new GroupJoinRequestMessageHandler();
        final GroupChatRequestMessageHandler groupChatHandler = new GroupChatRequestMessageHandler();
        final GroupMembersRequestMessageHandler groupMembersHandler = new GroupMembersRequestMessageHandler();
        final GroupQuitRequestMessageHandler groupQuitHandler = new GroupQuitRequestMessageHandler();
        final QuitHandler quitHandler = new QuitHandler();
        final ServerIdleStateHandler stateCheckHandler = new ServerIdleStateHandler();
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new IdleStateHandler(5, 0, 0))
                                    .addLast(stateCheckHandler)
                                    .addLast(new ProtocolFrameDecoder())
                                    .addLast(messageCodec)
//                                    .addLast(loggingHandler)
                                    .addLast(loginHandler)
                                    .addLast(chatHandler)
                                    .addLast(groupCreateHandler)
                                    .addLast(groupJoinHandler)
                                    .addLast(groupChatHandler)
                                    .addLast(groupMembersHandler)
                                    .addLast(groupQuitHandler)
                                    .addLast(quitHandler);

                        }
                    })
                    .bind(Config.getServerPort())
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
