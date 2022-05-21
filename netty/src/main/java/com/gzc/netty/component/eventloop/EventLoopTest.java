package com.gzc.netty.component.eventloop;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class EventLoopTest {

    public static void main(String[] args) {
        // IO 事件、普通任务、定时任务
        EventLoopGroup group = new NioEventLoopGroup();
        // EventLoop 数量 = 默认 cpu 核心数 * 2
        log.info(group.next().toString());
        log.info(group.next().toString());
        log.info(group.next().toString());

        // 执行普通任务
        group.next().submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("ok");
        });

        log.info("main");

        // 执行定时任务
        group.next().scheduleAtFixedRate(() -> {
            log.info("1");
        }, 0, 1, TimeUnit.SECONDS);
    }
}
