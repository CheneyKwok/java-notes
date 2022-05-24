package com.gzc.netty.component.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

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

    private static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append(buffer)
//                .append("read index:").append(buffer.readerIndex())
//                .append(" write index:").append(buffer.writerIndex())
//                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf);
    }
}
