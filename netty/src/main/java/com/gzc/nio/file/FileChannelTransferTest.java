package com.gzc.nio.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * 两个 Channel 传输数据
 */
public class FileChannelTransferTest {

    public static void main(String[] args) {
        try (FileChannel from = new FileInputStream("netty/from.txt").getChannel();
             FileChannel to = new FileOutputStream("netty/to.txt").getChannel()) {

            // 超过 2g 大小的文件传输
            long size = from.size();
            for (long left = size; left > 0;) {
                // 效率高，底层会利用操作系统的零拷贝进行优化
                left -= from.transferTo((size - left), left, to);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
