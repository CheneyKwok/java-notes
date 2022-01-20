package com.gzc;

import java.nio.ByteBuffer;

import static com.gzc.ByteBufferUtil.debugAll;

public class ByteBufferReadTest {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();
        System.out.println(buffer.get(new byte[4]));
        debugAll(buffer);
        buffer.rewind();
        System.out.println(buffer.get(new byte[4]));
        debugAll(buffer);


    }
}
