package com.gzc.nio.bytefuffer;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.gzc.nio.bytefuffer.ByteBufferUtil.debugAll;

@Slf4j
public class ByteBufferReadTest {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        debugAll(buffer);
        buffer.flip();
        byte[] out = new byte[4];
        buffer.get(out);
        log.info(Arrays.toString(out));
        debugAll(buffer);
        out = new byte[4];
        // 回退 从头开始读
        buffer.rewind();
        buffer.get(out);
        log.info(Arrays.toString(out));
        debugAll(buffer);
        buffer.rewind();
        log.info(String.valueOf((char)buffer.get()));
        log.info(String.valueOf((char)buffer.get()));
        // 标记位置
        buffer.mark();
        log.info(String.valueOf((char) buffer.get()));
        log.info(String.valueOf((char)buffer.get()));
        buffer.reset();
        log.info(String.valueOf((char)buffer.get()));
        log.info(String.valueOf((char)buffer.get()));



    }
}
