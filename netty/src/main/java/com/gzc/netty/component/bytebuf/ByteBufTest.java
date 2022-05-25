package com.gzc.netty.component.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.gzc.netty.component.bytebuf.ByteBufUtil.log;

public class ByteBufTest {

    public static void main(String[] args) {
        // 默认 capacity 256
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
//        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        log(byteBuf);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            sb.append("a");
        }
        byteBuf.writeBytes(sb.toString().getBytes());
        log(byteBuf);
    }
}
