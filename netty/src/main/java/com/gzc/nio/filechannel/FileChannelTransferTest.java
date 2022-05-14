package com.gzc.nio.filechannel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileChannelTransferTest {

    public static void main(String[] args) {
        try (FileChannel from = new FileInputStream("netty/from.txt").getChannel();
             FileChannel to = new FileOutputStream("netty/to.txt").getChannel()) {

            long size = from.size();
            for (long left = size; left > 0;) {
                left -= from.transferTo((size - left), left, to);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
