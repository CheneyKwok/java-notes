package com.gzc.nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WriteServer {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(9999));
        ssc.register(selector, SelectionKey.OP_ACCEPT, null);

        while (true) {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                if (key.isAcceptable()) {
                    ssc = (ServerSocketChannel) key.channel();
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < 90000; i++) {
                        builder.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(builder.toString());
                    int write = sc.write(buffer);
                    System.out.println(write);
                    for (int i = 0; i < 90000; i++) {
                        builder.append("a");
                    }
                    buffer = Charset.defaultCharset().encode(builder.toString());
                    if (buffer.hasRemaining()) {
                        // 追加可写事件
                        scKey.attach(buffer);
                        scKey.interestOps(scKey.interestOps() | SelectionKey.OP_WRITE);
                    }
                }

                if (key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();
                    int write = sc.write(buffer);
                    System.out.println(write);
                    if (!buffer.hasRemaining()) {
                        // 移除
                        sc.register(selector, key.interestOps() ^ SelectionKey.OP_WRITE, null);
                        key.attach(null);
                    }
                }
            }
        }
    }
}
