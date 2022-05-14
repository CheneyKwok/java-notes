package com.gzc.nio.bytefuffer;

import java.nio.ByteBuffer;

import static com.gzc.nio.bytefuffer.ByteBufferUtil.debugAll;

/**
 * 黏包、半包案例
 */
public class ByteBufferExamTest {

    public static void main(String[] args) {
        /**
         * 有多条数据发送给服务器，数据之间用 \n 进行分隔
         * 但由于某种原因这些数据在接受时，被进行了重新组合，例如原始数据有三条为
         * Hello,world\n
         * I'm zhangsan\n
         * How are you?\n
         * 变成了下面的两个ByteBuffer
         * Hello,world\nI'm zhangsan\nHo
         * w are you?\n
         *
         * 现要求你编写程序，将错乱的数据恢复成原始的按 \n 分隔的数据
         */

        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);

    }

    private static void split(ByteBuffer source) {
        // to read
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') {
                int len = i + 1 - source.position();
                ByteBuffer buffer = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    buffer.put(source.get());
                }
                debugAll(buffer);
            }
        }
        // to write
        source.compact();

    }
}
