package com.gzc.netty.component.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.gzc.netty.component.bytebuf.ByteBufUtil.log;

public class SliceTest {

    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        log(buf);

        ByteBuf f1 = buf.slice(0, 5);
        f1.retain();
        ByteBuf f2 = buf.slice(5, 5);
        f2.retain();
        f1.setByte(0, 'z');
        log(f1);
        log(f2);
        System.out.println("释放原有 ByteBuf 内存");
        buf.release();
        log(f1);
        log(f2);
    }
}
