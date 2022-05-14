package com.gzc.nio.bytefuffer;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static com.gzc.nio.bytefuffer.ByteBufferUtil.debugAll;

/**
 * 分散读取
 */
public class ScatteringReadsTest {

    public static void main(String[] args) {
        try (FileChannel channel = new RandomAccessFile("netty/words.text", "r").getChannel()) {
            ByteBuffer b1 = ByteBuffer.allocate(3);
            ByteBuffer b2 = ByteBuffer.allocate(3);
            ByteBuffer b3 = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{b1, b2, b3});
            b1.flip();
            b2.flip();
            b3.flip();
            debugAll(b1);
            debugAll(b2);
            debugAll(b3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
