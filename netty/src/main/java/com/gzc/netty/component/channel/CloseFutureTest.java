package com.gzc.netty.component.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class CloseFutureTest {

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture cf = ChannelFactory.getChannelFuture();
        Channel channel = cf.sync().channel();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String line;
            while ((line = scanner.next()) != null) {
                if (line.equals("q")) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(line);
            }
        }, "input").start();
        ChannelFuture closeFuture = channel.closeFuture();
//        testSync(closeFuture);
        testListener(closeFuture);
    }

    private static void testSync(ChannelFuture closeFuture) throws InterruptedException {
        log.info("关闭之前");
        // 主线程会在这里阻塞直到 channel 关闭
        closeFuture.sync();
        log.info("处理关闭之后的操作");
    }
    private static void testListener(ChannelFuture closeFuture) {
        closeFuture.addListener(future -> log.info("处理关闭之后的操作"));
    }

}
