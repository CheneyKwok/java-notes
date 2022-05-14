package com.gzc.nio.bytefuffer;



import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.gzc.nio.bytefuffer.ByteBufferUtil.debugAll;

public class ByteBufferStringTest {

    public static void main(String[] args) {
        // 字符串转 ByteBuffer
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes());
        debugAll(buffer1);

        // Charset auto flip
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2);
        // wrap auto flip
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer3);

        String s = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(s);
    }
}
