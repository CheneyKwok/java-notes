package com.gzc.nio.selector;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class WriteClient {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        boolean connect = sc.connect(new InetSocketAddress(9999));
        System.out.println("connect = " + connect);

        int count = 0;
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        while (true) {
            int read = sc.read(buffer);
            count += read;
            System.out.println(count);
            buffer.clear();
            if (read == -1) {
                break;
            }
        }
    }
}
