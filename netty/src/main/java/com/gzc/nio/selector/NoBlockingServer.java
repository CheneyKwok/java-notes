package com.gzc.nio.selector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.gzc.nio.bytefuffer.ByteBufferUtil.debugAll;

@Slf4j
public class NoBlockingServer {

    public static void main(String[] args) throws IOException {

        // 创建服务器
        ServerSocketChannel server = ServerSocketChannel.open();
        // 非阻塞模式
        server.configureBlocking(false);

        // 绑定端口
        server.bind(new InetSocketAddress(8888));

        // 连接集合
        List<SocketChannel> channels = new ArrayList<>();

        ByteBuffer buffer = ByteBuffer.allocate(16);

        while (true) {
            // accept 用来与客户端建立连接，SocketChannel用来与客户端之间通信
            SocketChannel channel = server.accept();
            if (channel != null) {
                // 非阻塞模式
                channel.configureBlocking(false);
                log.info("connected........{}", channel);
                channels.add(channel);
            }

            for (SocketChannel socketChannel : channels) {
                int read = socketChannel.read(buffer);
                if (read > 0) {
                    //　切换至读模式
                    buffer.flip();
                    debugAll(buffer);
                    buffer.clear();
                }
            }
        }
    }
}
