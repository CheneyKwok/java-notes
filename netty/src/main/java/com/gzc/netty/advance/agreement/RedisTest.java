package com.gzc.netty.advance.agreement;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

@Slf4j
public class RedisTest {

    /**
     * set name zhangsan // redis 将整个命令看作一个数组
     * *3                // 数组共有几个元素
     * $3                // set 元素的长度
     * set               // set 元素的内容
     * $4                // name 元素的长度
     * name              // name 元素的内容
     * $8                // zhangsan 元素的长度
     * zhangsan          // zhangsan 元素的内容
     */

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        byte[] LINE = {13, 10};
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("sending...");
                                            set(ctx);
                                            get(ctx);
                                        }

                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            ByteBuf buf = (ByteBuf) msg;
                                            System.out.println(buf.toString(Charset.defaultCharset()));
                                            super.channelRead(ctx, msg);
                                        }

                                        private void set(ChannelHandlerContext ctx) {
                                            ByteBuf buffer = ctx.alloc().buffer();
                                            buffer.writeBytes("*3".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("$3".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("set".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("$4".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("name".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("$8".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("zhangsan".getBytes());
                                            buffer.writeBytes(LINE);
                                            ctx.writeAndFlush(buffer);
                                        }

                                        private void get(ChannelHandlerContext ctx) {
                                            ByteBuf buffer = ctx.alloc().buffer();
                                            buffer.writeBytes("*2".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("$3".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("get".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("$4".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("name".getBytes());
                                            buffer.writeBytes(LINE);
                                            ctx.writeAndFlush(buffer);
                                        }
                                    });
                        }
                    })
                    .connect(new InetSocketAddress(6379))
                    .sync();
            log.info("connected {}", channelFuture.channel());
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
