package com.gzc.nio.socket;

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
public class Server {

    public static void main(String[] args) throws IOException {


        // 创建服务器 阻塞模式
        ServerSocketChannel server = ServerSocketChannel.open();

        // 绑定端口
        server.bind(new InetSocketAddress(8888));

        // 连接集合
        List<SocketChannel> channels = new ArrayList<>();

        ByteBuffer buffer = ByteBuffer.allocate(16);

        while (true) {
            // accept 用来与客户端建立连接，SocketChannel用来与客户端之间通信
            log.info("connecting.........");
            SocketChannel channel = server.accept();
            if (channel != null) {
                log.info("connected........");
                channels.add(channel);
            }

            for (SocketChannel socketChannel : channels) {
                log.info("before read.......{}", socketChannel);
                socketChannel.read(buffer);
                //　切换至读模式
                buffer.flip();
                debugAll(buffer);
                buffer.clear();
                log.info("after read");
            }
        }
    }
}
