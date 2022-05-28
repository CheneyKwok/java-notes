package com.gzc.netty.advance.resolve4;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Random;

/**
 * 固定分隔符
 */
@Slf4j
public class Client4 {

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            new Bootstrap()
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
                                                byte length = (byte) (r.nextInt(16) + 1);
                                                buffer.writeByte(length);
                                                for (int j = 0; j < length; j++) {
                                                    buffer.writeByte((byte) c);
                                                }
                                                c++;
                                            }
                                            ctx.writeAndFlush(buffer);
                                        }
                                    });
                        }
                    })
                    .connect(new InetSocketAddress(8080))
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }

    }
}
