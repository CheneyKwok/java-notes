package com.gzc.nio.selector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static com.gzc.nio.bytefuffer.ByteBufferUtil.debugAll;

@Slf4j
public class SelectorServer {

    public static void main(String[] args) throws IOException {

        // 创建 selector 来管理多个channel
        Selector selector = Selector.open();

        // 创建服务器
        ServerSocketChannel server = ServerSocketChannel.open();
        // 非阻塞模式
        server.configureBlocking(false);

        // 绑定端口
        server.bind(new InetSocketAddress(8888));

        SelectionKey sscKey = server.register(selector, 0, null);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);

        while (true) {
            // select 方法。没有事件发生，线程阻塞，否则线程恢复运行；有事件未处理也不会阻塞
            selector.select();
            // 处理事件，selectedKeys() 内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                // 处理完 key 时，需要从 selectedKeys 集合中删除，否则下次会重复处理
                iter.remove();
                log.info("key: {}", key);
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    sc.register(selector, SelectionKey.OP_READ, buffer);
                    log.info("{}", sc);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);
                        if (read == -1) {
                            key.cancel();
                        } else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel(); // 对于未处理的事件需要取消
                    }
                }
            }
        }
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
