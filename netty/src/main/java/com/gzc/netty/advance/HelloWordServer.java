package com.gzc.netty.advance;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloWordServer {

    void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {

            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_RCVBUF, 10) // 演示半包现象时打开
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("connected {}", ctx.channel());
                                            super.channelActive(ctx);
                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("disconnected {}", ctx.channel());
                                            super.channelInactive(ctx);
                                        }
                                    });
                        }
                    })
                    .bind(8080);
            Channel channel = channelFuture.channel();
            log.info("{} binding ...", channel);
            channelFuture.sync();
            log.info("{} bound ...", channel);
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            log.info("stopped");
        }
    }

    public static void main(String[] args) {
        new HelloWordServer().start();
    }
}
