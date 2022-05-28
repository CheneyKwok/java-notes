package com.gzc.netty.advance.resolve4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class LengthFieldDecoderTest {

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 1, 4),
                new LoggingHandler(LogLevel.DEBUG)
        );
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        send(buffer, "Hello, world!");
        send(buffer, "hi!");

        channel.writeInbound(buffer);
    }

    private static void send(ByteBuf buffer, String content) {
        byte[] bytes = content.getBytes();
        buffer.writeInt(bytes.length);
        buffer.writeByte(1);
        buffer.writeBytes(bytes);
    }
}
