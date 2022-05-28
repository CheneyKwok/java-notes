package com.gzc.netty.advance.resolve2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Random;

/**
 * 每一条消息采用固定长度
 */
@Slf4j
public class Client2 {

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("send...");
                                            Random r = new Random();
                                            char c = 'a';
                                            ByteBuf buffer = ctx.alloc().buffer();
                                            for (int i = 0; i < 10; i++) {
                                                byte[] bytes = new byte[8];
                                                for (int j = 0; j < r.nextInt(8); j++) {
                                                    bytes[j] = (byte) c;
                                                }
                                                c++;
                                                buffer.writeBytes(bytes);
                                            }
                                            ctx.writeAndFlush(buffer);
                                        }
                                    });
                        }
                    })
                    .connect(new InetSocketAddress(8080))
                    .sync();
            Channel channel = channelFuture.channel();
            log.info("connect {}", channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
