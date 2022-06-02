package com.gzc.netty.chatroom.client;

import com.gzc.netty.chatroom.message.*;
import com.gzc.netty.chatroom.protocol.MessageCodecSharable;
import com.gzc.netty.chatroom.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
public class ChatClient {

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();

        CountDownLatch waitForLogin = new CountDownLatch(1);
        AtomicBoolean isLogin = new AtomicBoolean(false);
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ProtocolFrameDecoder())
                                    .addLast(messageCodec)
//                                    .addLast(loggingHandler)
                                    .addLast("client-handler", new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            new Thread(() -> {
                                                Scanner scanner = new Scanner(System.in);
                                                System.out.println("请输入用户名");
                                                String username = scanner.nextLine();
                                                System.out.println("请输入密码");
                                                String password = scanner.nextLine();
                                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                                ctx.writeAndFlush(message);
                                                System.out.println("等待后续操作");
                                                try {
                                                    waitForLogin.await();
                                                } catch (InterruptedException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                if (!isLogin.get()) {
                                                    ctx.channel().close();
                                                    return;
                                                }
                                                while (true) {
                                                    System.out.println("==================================");
                                                    System.out.println("send [username] [content]");
                                                    System.out.println("gsend [group name] [content]");
                                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                                    System.out.println("gmembers [group name]");
                                                    System.out.println("gjoin [group name]");
                                                    System.out.println("gquit [group name]");
                                                    System.out.println("quit");
                                                    System.out.println("==================================");
                                                    String command = scanner.nextLine();
                                                    String[] s = command.split(" ");
                                                    switch (s[0]) {
                                                        case "send":
                                                            ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
                                                            break;
                                                        case "gsend":
                                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], s[2]));
                                                            break;
                                                        case "gcreate":
                                                            ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], Arrays.stream(s[2].split(",")).collect(Collectors.toSet())));
                                                            break;
                                                        case "gmembers":
                                                            ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                                            break;
                                                        case "gjoin":
                                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
                                                            break;
                                                        case "gquit":
                                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
                                                            break;
                                                        case "quit":
                                                            ctx.channel().close();
                                                            return;
                                                        default:break;

                                                    }
                                                }
                                            }, "system-in").start();
                                            super.channelActive(ctx);
                                        }

                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            log.debug("msg: {}", msg);
                                            if (msg instanceof LoginResponseMessage) {
                                                LoginResponseMessage responseMessage = (LoginResponseMessage) msg;
                                                if (responseMessage.isSuccess()) {
                                                    isLogin.set(true);
                                                }
                                                waitForLogin.countDown();
                                            }
                                        }
                                    });
                        }
                    })
                    .connect(new InetSocketAddress(8888));
            Channel channel = channelFuture.sync().channel();
            log.info("connected {}", channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error", e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
