package com.gzc.nio.file;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.gzc.nio.bytefuffer.ByteBufferUtil.debugAll;

@Slf4j
public class FileAIOTest {

    public static void main(String[] args) throws Exception {
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("netty/data.txt"), StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(16);
        log.info("read start");
        channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                log.info("read completed");
                log.info("result: {}", result);
                debugAll(attachment);
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                log.error("read error");
            }
        });
        Thread.sleep(1000);
        log.info("main end");
    }
}
