package com.gzc.nio.bytefuffer;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 集中写入
 */
public class ScatteringWriteTest {

    public static void main(String[] args) {
        ByteBuffer b1 = ByteBuffer.wrap("hello".getBytes());
        ByteBuffer b2 = ByteBuffer.wrap("world".getBytes());
        ByteBuffer b3 = ByteBuffer.wrap(" 你好".getBytes());
        try (FileChannel channel = new RandomAccessFile("netty/words2.text", "rw").getChannel()) {
            channel.write(new ByteBuffer[]{b1, b2, b3});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
