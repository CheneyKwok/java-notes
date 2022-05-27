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
