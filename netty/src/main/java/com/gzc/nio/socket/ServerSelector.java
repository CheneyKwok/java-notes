package com.gzc.nio.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.gzc.nio.bytefuffer.ByteBufferUtil.debugAll;

@Slf4j
public class ServerSelector {

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
                    sc.register(selector, SelectionKey.OP_READ, null);
                    log.info("{}", sc);
                } else if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    channel.read(buffer);
                    buffer.flip();
                    debugAll(buffer);
                }
            }
        }
    }
}
