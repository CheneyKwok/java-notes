package com.gzc.nio.bytefuffer;

import java.nio.ByteBuffer;

import static com.gzc.nio.bytefuffer.ByteBufferUtil.debugAll;

public class ByteBufferReadWriteTest {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61); // a
        debugAll(buffer);
        buffer.put(new byte[]{0x62, 0x63, 0x64}); //b c d
        debugAll(buffer);
        buffer.flip();
        System.out.println(buffer.get());
        debugAll(buffer);
        buffer.compact();
        debugAll(buffer);
        System.out.println(buffer.get());
        buffer.put(new byte[]{100, 2});
        debugAll(buffer);

    }
}
