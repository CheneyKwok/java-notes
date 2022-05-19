package com.gzc.nio.bytefuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class ByteBufferDemo {

    public static void main(String[] args) {
        // FileChannel
        // 获取方式：1. 输入输出流 2. new RandomAccessFile("data.txt", "r").getChannel()
        // 将文件写入 channel
        try (FileChannel channel = new FileInputStream("netty/data.txt").getChannel()) {
            // 申请缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                //将 channel 中的数据读出，向 buffer 写入
                int len = channel.read(buffer);
                if (len == -1) // 读完
                    break;
                log.info(" read byte count：{}", len);
                // 切换至读出模式
                buffer.flip();
                // 是否还有剩余未读数据
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    log.info("read byte：{}", (char) b);
                }
                // 切换至写入模式
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
