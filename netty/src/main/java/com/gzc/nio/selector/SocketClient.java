package com.gzc.nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class SocketClient {

    public static void main(String[] args) throws IOException {
        SocketChannel client = SocketChannel.open();

        client.connect(new InetSocketAddress(7777));

        client.write(StandardCharsets.UTF_8.encode("aaaabbbbccccddd3333\n"));

        System.in.read();

    }
}
