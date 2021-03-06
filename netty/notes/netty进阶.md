# Netty 进阶

## 粘包与半包

### 粘包现象

服务端代码

```java
public class HelloWordServer {

    void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {

            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("connected {}", ctx.channel());
                                            super.channelActive(ctx);
                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("disconnected {}", ctx.channel());
                                            super.channelInactive(ctx);
                                        }
                                    });
                        }
                    })
                    .bind(8080);
            Channel channel = channelFuture.channel();
            log.info("{} binding ...", channel);
            channelFuture.sync();
            log.info("{} bound ...", channel);
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            log.info("stopped");
        }
    }

    public static void main(String[] args) {
        new HelloWordServer().start();
    }
}
```

客户端代码

发送 10 个 消息，每个消息 16 字节

```java
public class HelloWorldClient {

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("sending...");
                                            Random random = new Random();
                                            char c = 'a';
                                            for (int i = 0; i < 10; i++) {
                                                ByteBuf buffer = ctx.alloc().buffer();
                                                byte[] bytes = new byte[16];
                                                for (int j = 0; j < 15; j++) {
                                                    bytes[j] = (byte) j;
                                                }
                                                buffer.writeBytes(bytes);
                                                ctx.writeAndFlush(buffer);
                                            }
                                        }
                                    });
                        }
                    })
                    .connect(new InetSocketAddress(8080));
            channelFuture.sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            worker.shutdownGracefully();
        }
    }
}
```

服务端出现一次接受 32 字节的情况

```java
15:43:25.078 [nioEventLoopGroup-3-2] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xd9aae970, L:/192.168.1.236:8080 - R:/192.168.1.236:49654] READ COMPLETE
15:43:25.078 [nioEventLoopGroup-3-2] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xd9aae970, L:/192.168.1.236:8080 - R:/192.168.1.236:49654] READ: 32B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 00 |................|
|00000010| 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 00 |................|
+--------+-------------------------------------------------+----------------+
```

### 半包现象


客户端发送 1 消息，大小为 160 字节

代码改为

```java
ByteBuf buffer = ctx.alloc().buffer();
for (int i = 0; i < 10; i++) {
    buffer.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
}
ctx.writeAndFlush(buffer);
```

为现象明显，修改服务器的缓冲区大小

```java
serverBootstrap.option(ChannelOption.SO_RCVBUF, 10)
```

服务器端可以看到接收的消息被分为两节，第一次 20 字节，第二次 140 字节

```java
15:56:21.845 [nioEventLoopGroup-3-2] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x49d5b335, L:/192.168.1.236:8080 - R:/192.168.1.236:49844] READ: 20B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f |................|
|00000010| 00 01 02 03                                     |....            |
+--------+-------------------------------------------------+----------------+
15:56:21.845 [nioEventLoopGroup-3-2] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledUnsafeDirectByteBuf(ridx: 0, widx: 20, cap: 2048) that reached at the tail of the pipeline. Please check your pipeline configuration.
15:56:21.845 [nioEventLoopGroup-3-2] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [LoggingHandler#0, HelloWordServer$1$1#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0x49d5b335, L:/192.168.1.236:8080 - R:/192.168.1.236:49844].
15:56:21.845 [nioEventLoopGroup-3-2] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x49d5b335, L:/192.168.1.236:8080 - R:/192.168.1.236:49844] READ COMPLETE
15:56:21.845 [nioEventLoopGroup-3-2] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x49d5b335, L:/192.168.1.236:8080 - R:/192.168.1.236:49844] READ: 140B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 00 01 02 03 |................|
|00000010| 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 00 01 02 03 |................|
|00000020| 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 00 01 02 03 |................|
|00000030| 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 00 01 02 03 |................|
|00000040| 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 00 01 02 03 |................|
|00000050| 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 00 01 02 03 |................|
|00000060| 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 00 01 02 03 |................|
|00000070| 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 00 01 02 03 |................|
|00000080| 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f             |............    |
+--------+-------------------------------------------------+----------------+
```

> **注意**
> serverBootstrap.option(ChannelOption.SO_RCVBUF, 10) 影响的是底层接收缓冲区（即滑动窗口）大小，仅决定了 netty 读取的最小单位，netty 实际每次读取的一般是其整数倍

### 现象分析

黏包

- 现象：发送 abc def，接受 abcdef
- 原因
  - 应用层：接收方 ByteBuf 设置太大 (Netty 默认 1024)
  - 滑动窗口：假设发送方 256 表示一个完整报文，但由于接收方处理不及时且滑动窗口足够大，这 256 字节就会缓冲在接收方的窗口中，当窗口缓冲了多个报文就会粘包
  - Nagle 算法会造成粘包

半包

- 现象：发送 abcdef，接受 abc def
- 原因
  - 应用层：接收方 Bytebuf 小于实际发送数量
  - 滑动窗口：假设接收方的窗口只剩了 128 字节，发送方的报文大小是 256 字节，这是无法放下，只能先发送前 128，等待 ack 后才能发送剩余部分，造成半包
  - MSS 限制：当发送的数据超过 MSS 限制后，会将数据切分发送，造成半包

本质是因为 TCP 是流式协议，消息无边界

> 滑动窗口
> TCP 一个段 (segment) 为单位，每发送一个段就需要进行一次确认 (ack) 处理，但是这样做的缺点是包的往返时间越长性能越差
>
> ![图 1](../../.image/dd68493fe8ed5390bd97a21d2c2ecc879103a7c695ef4f1d12628227a3803857.png)  
>
> 为了解决此问题，引入了窗口概念，即决定了无需等待应答而可以继续发送的数据最大值
>
> ![图 2](../../.image/79766373c3c00f5c74a40aabfef89566537722f91780aed692050bc448d14496.png) 
>
> - 窗口实际起到一个缓冲区的作用，同时也能起到流量控制的作用
>
> MSS 限制
>
> - 链路层对一次能够发送的最大数据有限制，这个限制称之为 MTU (maximum transmission unit)，不同的链路设备的 MTU 值也有所不同，例如
>   - 以太网的 MTU 是 1500
>   - FDDI（光纤分布式数据接口）是 4352
>   - 本地回环地址是 65535，不走网卡
> - MSS 是最大段长度 (maximum segment size)，它是 MTU 刨去 tcp 头和 ip 头后剩余能够作为数据传输的字节数
>   - ipv4 tcp 头占用 20 bytes，ip 头占用 20 bytes，因此以太网 MSS 的值为 1500 - 40 = 1460
>   - TCP 在传递大量数据时，会按照 MSS 大小将数据进行分割发送
>   - MSS 的值在第三次握手时通知对方自己的 MSS 值，然后在两者之间选一个最小值作为 MSS
>
> ![图 3](../../.image/34e54af17437baa735712b13b410f6673ec0af2fc7f5c689beb0fc962f072019.png)  
>
> Nagle 算法
>
> - 即使发送一个字节，也需要加入 tcp 头和 ip 头，也就是总字节数会使用 41 字节，非常不经济。因此为了提高网络利用率, tcp 希望尽可能发送足够大的数据，这就是 Nagle 算法产生的原因
> - 该算法指的是：发送端即使还有应该发送的数据，但如果这部分数据很少的话，则进行延迟发送
>   - 如果 SO_SNDBUF 的数据达到 MSS，则需要发送
>   - 如果 SO_SNDBUF 中含有 FIN (表示需要连接关闭)，这时将剩余数据发送，再关闭
>   - 如果 TCP_NODELAY = true，则需要发送
>   - 已发送的数据都收到 ack，则需要发送
>   - 上述条件不满足，但发生超市，则需要发送
>   - 除上述情况，延迟发送

### 解决方案

1. 短链接，发一个包建立一次连接，这样连接建立到连接断开之间就是消息的边界，缺点效率太低
2. 每一条消息采用固定长度，缺点浪费空间
3. 每一个消息采用分隔符，例如 \n，缺点需要转义
4. 每一条消息分为 head 和 body，head 中包含 body 的长度

#### 方法1：短链接

短链接可以解决粘包，不能避免半包

```java
public class Client1 {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            send();
        }
        log.info("send end");
    }

    private static void send() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("sending...");
                                            ByteBuf buffer = ctx.alloc().buffer();
                                            buffer.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
                                            ctx.writeAndFlush(buffer);
                                            ctx.close();
                                        }
                                    });
                        }
                    })
                    .connect(new InetSocketAddress(8080))
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        } finally {
            worker.shutdownGracefully();
        }
    }
}
```

#### 方法2，固定长度

让所有数据包长度固定（假设长度为 8 字节），服务器端加入

```java
ch.pipeline().addLast(new FixedLengthFrameDecoder(8));
```

客户端测试代码，注意, 采用这种方法后，客户端什么时候 flush 都可以

```java
public class Client2 {

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("send...");
                                            Random r = new Random();
                                            char c = 'a';
                                            ByteBuf buffer = ctx.alloc().buffer();
                                            for (int i = 0; i < 10; i++) {
                                                byte[] bytes = new byte[8];
                                                for (int j = 0; j < r.nextInt(8); j++) {
                                                    bytes[j] = (byte) c;
                                                }
                                                c++;
                                                buffer.writeBytes(bytes);
                                            }
                                            ctx.writeAndFlush(buffer);
                                        }
                                    });
                        }
                    })
                    .connect(new InetSocketAddress(8080))
                    .sync();
            Channel channel = channelFuture.channel();
            log.info("connect {}", channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
```

客户端输出

```java
15:01:46.825 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xea0f9146, L:/172.21.32.1:65328 - R:0.0.0.0/0.0.0.0:8080] WRITE: 80B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 00 00 00 00 00 62 00 00 00 00 00 00 00 |........b.......|
|00000010| 63 63 63 63 63 00 00 00 64 64 64 64 64 00 00 00 |ccccc...ddddd...|
|00000020| 65 65 00 00 00 00 00 00 66 66 66 00 00 00 00 00 |ee......fff.....|
|00000030| 67 67 00 00 00 00 00 00 68 68 00 00 00 00 00 00 |gg......hh......|
|00000040| 69 69 69 69 69 69 00 00 6a 6a 6a 00 00 00 00 00 |iiiiii..jjj.....|
+--------+-------------------------------------------------+----------------+
15:01:46.826 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xea0f9146, L:/172.21.32.1:65328 - R:0.0.0.0/0.0.0.0:8080] FLUSH
```

服务端输出

```java
15:01:46.864 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328] READ: 8B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 00 00 00 00 00                         |........        |
+--------+-------------------------------------------------+----------------+
15:01:46.864 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 8, cap: 8/8, unwrapped: PooledUnsafeDirectByteBuf(ridx: 8, widx: 80, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328].
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328] READ: 8B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 62 00 00 00 00 00 00 00                         |b.......        |
+--------+-------------------------------------------------+----------------+
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 8, cap: 8/8, unwrapped: PooledUnsafeDirectByteBuf(ridx: 16, widx: 80, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328].
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328] READ: 8B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 63 63 63 63 63 00 00 00                         |ccccc...        |
+--------+-------------------------------------------------+----------------+
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 8, cap: 8/8, unwrapped: PooledUnsafeDirectByteBuf(ridx: 24, widx: 80, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328].
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328] READ: 8B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 64 64 64 64 64 00 00 00                         |ddddd...        |
+--------+-------------------------------------------------+----------------+
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 8, cap: 8/8, unwrapped: PooledUnsafeDirectByteBuf(ridx: 32, widx: 80, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328].
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328] READ: 8B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 65 65 00 00 00 00 00 00                         |ee......        |
+--------+-------------------------------------------------+----------------+
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 8, cap: 8/8, unwrapped: PooledUnsafeDirectByteBuf(ridx: 40, widx: 80, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328].
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328] READ: 8B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 66 66 66 00 00 00 00 00                         |fff.....        |
+--------+-------------------------------------------------+----------------+
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 8, cap: 8/8, unwrapped: PooledUnsafeDirectByteBuf(ridx: 48, widx: 80, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328].
15:01:46.865 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328] READ: 8B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 67 67 00 00 00 00 00 00                         |gg......        |
+--------+-------------------------------------------------+----------------+
15:01:46.866 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 8, cap: 8/8, unwrapped: PooledUnsafeDirectByteBuf(ridx: 56, widx: 80, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
15:01:46.866 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328].
15:01:46.866 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328] READ: 8B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 68 68 00 00 00 00 00 00                         |hh......        |
+--------+-------------------------------------------------+----------------+
15:01:46.866 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 8, cap: 8/8, unwrapped: PooledUnsafeDirectByteBuf(ridx: 64, widx: 80, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
15:01:46.866 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328].
15:01:46.866 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328] READ: 8B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 69 69 69 69 69 69 00 00                         |iiiiii..        |
+--------+-------------------------------------------------+----------------+
15:01:46.866 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 8, cap: 8/8, unwrapped: PooledUnsafeDirectByteBuf(ridx: 72, widx: 80, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
15:01:46.866 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328].
15:01:46.866 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x243044ac, L:/172.21.32.1:8080 - R:/172.21.32.1:65328] READ: 8B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 6a 6a 6a 00 00 00 00 00                         |jjj.....        |
+--------+-------------------------------------------------+----------------+
```

缺点是，数据包的大小不好把握

- 长度定的太大，浪费
- 长度定的太小，对某些数据包又显得不够

#### 方法3：固定分隔符

服务端加入，默认以 \n 或 \r\n 作为分隔符，如果超出指定长度仍未出现分隔符，则抛出异常

```java
ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
```

客户端在每条消息之后，加入 \n 分隔符

```java
public class Client3 {

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("send...");
                                            Random r = new Random();
                                            char c = 'a';
                                            ByteBuf buffer = ctx.alloc().buffer();
                                            for (int i = 0; i < 10; i++) {
                                                for (int j = 0; j < r.nextInt(16) + 1; j++) {
                                                    buffer.writeByte((byte) c);
                                                }
                                                buffer.writeByte('\n');
                                                c++;
                                            }
                                            ctx.writeAndFlush(buffer);
                                        }
                                    });
                        }
                    })
                    .connect(new InetSocketAddress(8080))
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }

    }
}
```

客户端输出

```java
16:09:33.695 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xe4592c38, L:/172.21.32.1:49418 - R:0.0.0.0/0.0.0.0:8080] WRITE: 47B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 0a 62 62 62 62 0a 63 63 63 0a 64 64 64 64 64 |a.bbbb.ccc.ddddd|
|00000010| 64 64 64 64 0a 65 65 65 65 0a 66 66 66 0a 67 67 |dddd.eeee.fff.gg|
|00000020| 67 0a 68 68 68 68 0a 69 69 0a 6a 6a 6a 6a 0a    |g.hhhh.ii.jjjj. |
+--------+-------------------------------------------------+----------------+
```

服务端输出

```java
16:09:33.741 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xeec0c5ce, L:/172.21.32.1:8080 - R:/172.21.32.1:49418] READ: 1B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61                                              |a               |
+--------+-------------------------------------------------+----------------+
16:09:33.741 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 1, cap: 1/1, unwrapped: PooledUnsafeDirectByteBuf(ridx: 2, widx: 47, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
16:09:33.741 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [LineBasedFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xeec0c5ce, L:/172.21.32.1:8080 - R:/172.21.32.1:49418].
16:09:33.741 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xeec0c5ce, L:/172.21.32.1:8080 - R:/172.21.32.1:49418] READ: 4B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 62 62 62 62                                     |bbbb            |
+--------+-------------------------------------------------+----------------+
16:09:33.741 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 4, cap: 4/4, unwrapped: PooledUnsafeDirectByteBuf(ridx: 7, widx: 47, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
16:09:33.741 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [LineBasedFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xeec0c5ce, L:/172.21.32.1:8080 - R:/172.21.32.1:49418].
16:09:33.741 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xeec0c5ce, L:/172.21.32.1:8080 - R:/172.21.32.1:49418] READ: 3B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 63 63 63                                        |ccc             |
+--------+-------------------------------------------------+----------------+
16:09:33.741 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 3, cap: 3/3, unwrapped: PooledUnsafeDirectByteBuf(ridx: 11, widx: 47, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
16:09:33.741 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [LineBasedFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xeec0c5ce, L:/172.21.32.1:8080 - R:/172.21.32.1:49418].
16:09:33.741 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xeec0c5ce, L:/172.21.32.1:8080 - R:/172.21.32.1:49418] READ: 9B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 64 64 64 64 64 64 64 64 64                      |ddddddddd       |
+--------+-------------------------------------------------+----------------+

...
```

#### 方法4：预设长度

服务端增加

```java
// 最大长度、长度偏移量、长度占用字节、长度调整补偿值、需要跳过的字节数
.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 1, 0, 1))
```

客户端

```java
public class Client4 {

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("send...");
                                            Random r = new Random();
                                            char c = 'a';
                                            ByteBuf buffer = ctx.alloc().buffer();
                                            for (int i = 0; i < 10; i++) {
                                                byte length = (byte) (r.nextInt(16) + 1);
                                                buffer.writeByte(length);
                                                for (int j = 0; j < length; j++) {
                                                    buffer.writeByte((byte) c);
                                                }
                                                c++;
                                            }
                                            ctx.writeAndFlush(buffer);
                                        }
                                    });
                        }
                    })
                    .connect(new InetSocketAddress(8080))
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }

    }
}
```

客户端输出

```java
17:22:14.322 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x99c2f6fe, L:/172.21.32.1:49971 - R:0.0.0.0/0.0.0.0:8080] WRITE: 93B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 07 61 61 61 61 61 61 61 0a 62 62 62 62 62 62 62 |.aaaaaaa.bbbbbbb|
|00000010| 62 62 62 0d 63 63 63 63 63 63 63 63 63 63 63 63 |bbb.cccccccccccc|
|00000020| 63 0a 64 64 64 64 64 64 64 64 64 64 0a 65 65 65 |c.dddddddddd.eee|
|00000030| 65 65 65 65 65 65 65 01 66 07 67 67 67 67 67 67 |eeeeeee.f.gggggg|
|00000040| 67 07 68 68 68 68 68 68 68 06 69 69 69 69 69 69 |g.hhhhhhh.iiiiii|
|00000050| 0c 6a 6a 6a 6a 6a 6a 6a 6a 6a 6a 6a 6a          |.jjjjjjjjjjjj   |
+--------+-------------------------------------------------+----------------+
```

服务端输出

```java
17:22:14.371 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xda71d47c, L:/172.21.32.1:8080 - R:/172.21.32.1:49971] READ: 7B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 61 61 61 61 61 61                            |aaaaaaa         |
+--------+-------------------------------------------------+----------------+
17:22:14.371 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 7, cap: 7/7, unwrapped: PooledUnsafeDirectByteBuf(ridx: 8, widx: 93, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
17:22:14.371 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [LengthFieldBasedFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xda71d47c, L:/172.21.32.1:8080 - R:/172.21.32.1:49971].
17:22:14.371 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xda71d47c, L:/172.21.32.1:8080 - R:/172.21.32.1:49971] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 62 62 62 62 62 62 62 62 62 62                   |bbbbbbbbbb      |
+--------+-------------------------------------------------+----------------+
17:22:14.371 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 10, cap: 10/10, unwrapped: PooledUnsafeDirectByteBuf(ridx: 19, widx: 93, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
17:22:14.371 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [LengthFieldBasedFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xda71d47c, L:/172.21.32.1:8080 - R:/172.21.32.1:49971].
17:22:14.371 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xda71d47c, L:/172.21.32.1:8080 - R:/172.21.32.1:49971] READ: 13B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 63 63 63 63 63 63 63 63 63 63 63 63 63          |ccccccccccccc   |
+--------+-------------------------------------------------+----------------+
17:22:14.371 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 13, cap: 13/13, unwrapped: PooledUnsafeDirectByteBuf(ridx: 33, widx: 93, cap: 2048)) that reached at the tail of the pipeline. Please check your pipeline configuration.
17:22:14.372 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [LengthFieldBasedFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xda71d47c, L:/172.21.32.1:8080 - R:/172.21.32.1:49971].
17:22:14.372 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xda71d47c, L:/172.21.32.1:8080 - R:/172.21.32.1:49971] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 64 64 64 64 64 64 64 64 64 64                   |dddddddddd      |
+--------+-------------------------------------------------+----------------+

...
```

## 协议设计与解析

### 为什么需要协议

TCP/IP 中消息传输基于流方式，没有边界

协议的目的是协定消息的边界，制定通信双方要共同遵守的通信规则

### redis 协议举例

```java
public class redisTest {

    /**
     * set name zhangsan // redis 将整个命令看作一个数组
     * *3                // 数组共有几个元素
     * $3                // set 元素的长度
     * set               // set 元素的内容
     * $4                // name 元素的长度
     * name              // name 元素的内容
     * $8                // zhangsan 元素的长度
     * zhangsan          // zhangsan 元素的内容
     */

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        byte[] LINE = {13, 10};
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("sending...");
                                            set(ctx);
                                            get(ctx);
                                        }

                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            ByteBuf buf = (ByteBuf) msg;
                                            System.out.println(buf.toString(Charset.defaultCharset()));
                                            super.channelRead(ctx, msg);
                                        }

                                        private void set(ChannelHandlerContext ctx) {
                                            ByteBuf buffer = ctx.alloc().buffer();
                                            buffer.writeBytes("*3".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("$3".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("set".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("$4".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("name".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("$8".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("zhangsan".getBytes());
                                            buffer.writeBytes(LINE);
                                            ctx.writeAndFlush(buffer);
                                        }

                                        private void get(ChannelHandlerContext ctx) {
                                            ByteBuf buffer = ctx.alloc().buffer();
                                            buffer.writeBytes("*2".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("$3".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("get".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("$4".getBytes());
                                            buffer.writeBytes(LINE);
                                            buffer.writeBytes("name".getBytes());
                                            buffer.writeBytes(LINE);
                                            ctx.writeAndFlush(buffer);
                                        }
                                    });
                        }
                    })
                    .connect(new InetSocketAddress(6379))
                    .sync();
            log.info("connected {}", channelFuture.channel());
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
```

输出

```java
14:28:49.806 [nioEventLoopGroup-2-1] INFO com.gzc.netty.advance.agreement.RedisTest - sending...
14:28:49.812 [nioEventLoopGroup-2-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxCapacityPerThread: 4096
14:28:49.812 [nioEventLoopGroup-2-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxSharedCapacityFactor: 2
14:28:49.812 [nioEventLoopGroup-2-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.linkCapacity: 16
14:28:49.812 [nioEventLoopGroup-2-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.ratio: 8
14:28:49.812 [nioEventLoopGroup-2-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.delayedQueue.ratio: 8
14:28:49.807 [main] INFO com.gzc.netty.advance.agreement.RedisTest - connected [id: 0xb498eb74, L:/172.21.32.1:52139 - R:0.0.0.0/0.0.0.0:6379]
14:28:49.823 [nioEventLoopGroup-2-1] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkAccessible: true
14:28:49.823 [nioEventLoopGroup-2-1] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkBounds: true
14:28:49.824 [nioEventLoopGroup-2-1] DEBUG io.netty.util.ResourceLeakDetectorFactory - Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@4cc5aa28
+OK
$8
zhangsan
```

### http 协议举例

```java
public class HttpTest {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new HttpServerCodec())
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws Exception {
                                            log.info(httpRequest.uri());
                                            DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);
                                            byte[] bytes = "<h1> Hello, World!</h1>".getBytes();
                                            response.headers().setInt(CONTENT_LENGTH, bytes.length);
                                            response.content().writeBytes(bytes);
                                            ctx.writeAndFlush(response);
                                        }
                                    });
                        }
                    })
                    .bind(8080)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
```

输出

```text
17:18:51.096 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x111f9c13, L:/127.0.0.1:8080 - R:/127.0.0.1:59211] READ: DefaultHttpRequest(decodeResult: success, version: HTTP/1.1)
GET /index HTTP/1.1
Host: 127.0.0.1:8080
Connection: keep-alive
Cache-Control: max-age=0
sec-ch-ua: " Not A;Brand";v="99", "Chromium";v="101", "Google Chrome";v="101"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "Windows"
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.41 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
Sec-Fetch-Site: cross-site
Sec-Fetch-Mode: navigate
Sec-Fetch-User: ?1
Sec-Fetch-Dest: document
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9
Cookie: JSESSIONID_wanmei_fish_manager=55574b58-a8b0-4f3e-88bf-32a2b688c7d0; login_proxy_sale=1653811757390
17:18:51.096 [nioEventLoopGroup-3-1] INFO com.gzc.netty.advance.agreement.HttpTest - /index
17:18:51.100 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x111f9c13, L:/127.0.0.1:8080 - R:/127.0.0.1:59211] WRITE: DefaultFullHttpResponse(decodeResult: success, version: HTTP/1.1, content: UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf(ridx: 0, widx: 23, cap: 64))
HTTP/1.1 200 OK
content-length: 23, 23B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 3c 68 31 3e 20 48 65 6c 6c 6f 2c 20 57 6f 72 6c |<h1> Hello, Worl|
|00000010| 64 21 3c 2f 68 31 3e                            |d!</h1>         |
+--------+-------------------------------------------------+----------------+
```

### 自定义协议要素

- 魔数：用来第一时间判断是否是无效数据包
- 版本号：可以支持协议升级
- 序列化算法：消息正文到底采用哪种序列化反序列化方式，可以由此扩展，如 json、protobuf、hessian、jdk
- 指令类型：是登录、注册、单聊、群聊... 跟业务相关
- 请求序号：为了双工通信，提高异步能力
- 正文长度
- 消息正文

#### 编解码器

设计一个登录请求消息和登录响应消息，并使用 Netty 完成收发

```java
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 1. 4 字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 1 字节的版本,
        out.writeByte(1);
        // 3. 1 字节的序列化方式 jdk 0 , json 1
        out.writeByte(0);
        // 4. 1 字节的指令类型
        out.writeByte(msg.getMessageType());
        // 5. 4 个字节
        out.writeInt(msg.getSequenceId());
        // 无意义，对齐填充
        out.writeByte(0xff);
        // 6. 获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        // 7. 长度
        out.writeInt(bytes.length);
        // 8. 写入内容
        out.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();
        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("{}", message);
        out.add(message);
    }
}
```

测试

```java
EmbeddedChannel channel = new EmbeddedChannel(
    new LoggingHandler(),
    new LengthFieldBasedFrameDecoder(
        1024, 12, 4, 0, 0),
    new MessageCodec()
);
// encode
LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123", "张三");
//        channel.writeOutbound(message);
// decode
ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
new MessageCodec().encode(null, message, buf);

ByteBuf s1 = buf.slice(0, 100);
ByteBuf s2 = buf.slice(100, buf.readableBytes() - 100);
s1.retain(); // 引用计数 2
channel.writeInbound(s1); // release 1
channel.writeInbound(s2);
```

#### 💡 什么时候可以加 @Sharable

- 当 handler 不保存状态时，就可以安全地在多线程下被共享
- 但要注意对于编解码器类，不能继承 ByteToMessageCodec 或 CombinedChannelDuplexHandler 父类，他们的构造方法对 @Sharable 有限制
- 如果能确保编解码器不会保存状态，可以继承 MessageToMessageCodec 父类

```java
@Slf4j
@ChannelHandler.Sharable
/**
 * 必须和 LengthFieldBasedFrameDecoder 一起使用，确保接到的 ByteBuf 消息是完整的
 */
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 1. 4 字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 1 字节的版本,
        out.writeByte(1);
        // 3. 1 字节的序列化方式 jdk 0 , json 1
        out.writeByte(0);
        // 4. 1 字节的指令类型
        out.writeByte(msg.getMessageType());
        // 5. 4 个字节
        out.writeInt(msg.getSequenceId());
        // 无意义，对齐填充
        out.writeByte(0xff);
        // 6. 获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        // 7. 长度
        out.writeInt(bytes.length);
        // 8. 写入内容
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();
        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("{}", message);
        out.add(message);
    }
}
```

### 聊天室业务-单聊

服务器端将 handler 独立出来

登录 handler

```java
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage message;
        if(login) {
            SessionFactory.getSession().bind(ctx.channel(), username);
            message = new LoginResponseMessage(true, "登录成功");
        } else {
            message = new LoginResponseMessage(false, "用户名或密码不正确");
        }
        ctx.writeAndFlush(message);
    }
}
```

单聊 handler

```java
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        // 在线
        if(channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
        // 不在线
        else {
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方用户不存在或者不在线"));
        }
    }
}
```

### 3.4 聊天室业务-群聊

创建群聊

```java
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        // 群管理器
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group == null) {
            // 发生成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, groupName + "创建成功"));
            // 发送拉群消息
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            for (Channel channel : channels) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入" + groupName));
            }
        } else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, groupName + "已经存在"));
        }
    }
}
```

群聊

```java
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        List<Channel> channels = GroupSessionFactory.getGroupSession()
                .getMembersChannel(msg.getGroupName());

        for (Channel channel : channels) {
            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
    }
}
```

加入群聊

```java
@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        Group group = GroupSessionFactory.getGroupSession().joinMember(msg.getGroupName(), msg.getUsername());
        if (group != null) {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, msg.getGroupName() + "群加入成功"));
        } else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, msg.getGroupName() + "群不存在"));
        }
    }
}
```

退出群聊

```java
@ChannelHandler.Sharable
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        Group group = GroupSessionFactory.getGroupSession().removeMember(msg.getGroupName(), msg.getUsername());
        if (group != null) {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, "已退出群" + msg.getGroupName()));
        } else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, msg.getGroupName() + "群不存在"));
        }
    }
}
```

查看成员

```java
@ChannelHandler.Sharable
public class GroupMembersRequestMessageHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        Set<String> members = GroupSessionFactory.getGroupSession()
                .getMembers(msg.getGroupName());
        ctx.writeAndFlush(new GroupMembersResponseMessage(members));
    }
}
```

### 3.5 聊天室业务-退出

```java
@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {

    // 当连接断开时触发 inactive 事件
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} 已经断开", ctx.channel());
    }

    // 当出现异常时触发
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} 已经异常断开 异常是{}", ctx.channel(), cause.getMessage());
    }
}
```

### 聊天室业务-空闲检测

#### 连接假死

原因

- 网络设备出现故障，例如网卡，机房等，底层的 TCP 连接已经断开了，但应用程序没有感知到，仍然占用着资源。
- 公网网络不稳定，出现丢包。如果连续出现丢包，这时现象就是客户端数据发不出去，服务端也一直收不到数据，就这么一直耗着
- 应用程序线程阻塞，无法进行数据读写

问题

- 假死的连接占用的资源不能自动释放
- 向假死的连接发送数据，得到的反馈是发送超时

服务器端解决

- 怎么判断客户端连接是否假死呢？如果能收到客户端数据，说明没有假死。因此策略就可以定为，每隔一段时间就检查这段时间内是否接收到客户端数据，没有就可以判定为连接假死

```java
// 用来判断是不是 读空闲时间过长，或 写空闲时间过长
// 5s 内如果没有收到 channel 的数据，会触发一个 IdleState#READER_IDLE 事件
ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
// ChannelDuplexHandler 可以同时作为入站和出站处理器
ch.pipeline().addLast(new ChannelDuplexHandler() {
    // 用来触发特殊事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
        IdleStateEvent event = (IdleStateEvent) evt;
        // 触发了读空闲事件
        if (event.state() == IdleState.READER_IDLE) {
            log.debug("已经 5s 没有读到数据了");
            ctx.channel().close();
        }
    }
});
```

客户端定时心跳

- 客户端可以定时向服务器端发送数据，只要这个时间间隔小于服务器定义的空闲检测的时间间隔，那么就能防止前面提到的误判，客户端可以定义如下心跳处理器

```java
// 用来判断是不是 读空闲时间过长，或写空闲时间过长
// 3s 内如果没有向服务器写数据，会触发一个 IdleState#WRITER_IDLE 事件
ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
// ChannelDuplexHandler 可以同时作为入站和出站处理器
ch.pipeline().addLast(new ChannelDuplexHandler() {
    // 用来触发特殊事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
        IdleStateEvent event = (IdleStateEvent) evt;
        // 触发了写空闲事件
        if (event.state() == IdleState.WRITER_IDLE) {
            //                                log.debug("3s 没有写数据了，发送一个心跳包");
            ctx.writeAndFlush(new PingMessage());
        }
    }
});
```
