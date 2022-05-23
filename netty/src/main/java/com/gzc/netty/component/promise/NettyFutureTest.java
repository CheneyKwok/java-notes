package com.gzc.netty.component.promise;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NettyFutureTest {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();
        Future<Integer> future = eventLoop.submit(() -> {
            log.info("执行计算");
            Thread.sleep(1000);
            return 50;
        });

        // 同步
//        log.info("等待结果");
//        log.info("结果是 {}", future.get());

        // 异步
        future.addListener(f -> log.info("接受结果 {}", f.getNow()));
    }

}
