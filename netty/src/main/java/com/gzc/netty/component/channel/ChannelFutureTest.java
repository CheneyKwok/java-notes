package com.gzc.netty.component.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ChannelFutureTest {

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture cf = ChannelFactory.getChannelFuture();
//        testSync(cf);
        testListener(cf);
    }

    private static void testListener(ChannelFuture cf) {
        cf.addListener((ChannelFutureListener) future -> {
            Channel channel = future.channel();
            channel.writeAndFlush("hello");
        });
    }

    private static void testSync(ChannelFuture cf) throws InterruptedException {
        Channel channel = cf.sync().channel();
        channel.writeAndFlush("hello");
    }
}
