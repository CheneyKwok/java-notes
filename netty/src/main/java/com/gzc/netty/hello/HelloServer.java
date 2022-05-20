package com.gzc.netty.hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {

    public static void main(String[] args) {
        // 启动器（负责组装 Netty 组件吗，启动服务器）
        new ServerBootstrap()
                // 添加 BossEventLoop、WorkerEventLoop，即 selector 和 thead 的封装
                // Boss(parent) 负责处理连接，Worker(child) 负责处理读写
                .group(new NioEventLoopGroup())
                // 选择服务器的 ServerSocketChannel 实现
                .channel(NioServerSocketChannel.class)
                // Worker(child) 能处理哪些操作(handler)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    // ChannelInitializer 负责和客户端进行数据读写通道的初始化
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 添加具体的handler
                        // 解码器
                        ch.pipeline().addLast(new StringDecoder());
                        // 自定义 handler 处理读事件
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                .bind(6666);

    }
}
