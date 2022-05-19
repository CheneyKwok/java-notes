package com.gzc.nio.selector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.gzc.nio.bytefuffer.ByteBufferUtil.debugAll;

@Slf4j
public class MultiThreadServer {

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(7777));
        server.register(selector, SelectionKey.OP_ACCEPT, null);

        while (true) {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();
                    socketChannel.configureBlocking(false);
                    log.info("connected successfully: {}", socketChannel.getRemoteAddress());
                    Worker worker = new Worker("worker-0");
                    worker.register(socketChannel);
                    worker.selector.wakeup();
                }
            }
        }
    }

    static class Worker implements Runnable {
        private Thread thread;

        private Selector selector;

        private String name;

        private volatile boolean start;

        private ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;

        }

        public void register(SocketChannel socketChannel) {
            try {
                if (!start) {
                    selector = Selector.open();
                    thread = new Thread(this, name);
                    thread.start();
                    start = true;
                }

                taskQueue.add(() -> {
                    try {
                        socketChannel.register(selector, SelectionKey.OP_READ, null);
                    } catch (ClosedChannelException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        @Override
        public void run() {

            while (true) {
                try {
                    selector.select();
                    Runnable task = taskQueue.poll();
                    if (task != null) {
                        task.run();
                    }
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(30);
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
