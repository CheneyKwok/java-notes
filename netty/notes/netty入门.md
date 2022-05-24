# Netty 入门

## 概述

Netty 是什么

```java
Netty is an asynchronous event-driven network application framework for rapid development of maintainable high perform protocol servers & clients.
```

Netty 是一个异步的、基于事件驱动的网络应用框架，用于快速开发可维护、高性能的网络服务器和客户端。

Netty 的优势

- Netty VS NIO
  - 工作量大，bug多
  - 需要自己构建协议
  - 解决 TCP 传输问题，如黏包、半包
  - epoll 空轮询导致 CPU 100%
  - 对 API 进行增强，使之更易用，如 FastThreadLocal => ThreadLocal，ByteBuf => ByteBuffer
- Netty VS 其他网络应用框架
  -Mina 由 apache 维护，将来 3.x 版本可能会有较大重构，破坏 API 向下兼容性，Netty 的开发迭代更迅速，API 更简洁、文档更优秀
  - 久经考验，16年
    - 2.x 2004
    - 3.x 2008
    - 4.x 2013
    - 5.x 已废弃（没有明显的性能提升，维护成本高）

## Hello World

### 目标

开发一个简单的服务器端和客户端

- 客户端向服务器发送 hello, world
- 服务器仅接收，不返回

加入依赖

```java
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.39.Final</version>
</dependency>
```

### 服务器端

```java
public class HelloServer {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup()) // 1
                .channel(NioServerSocketChannel.class) // 2
                .childHandler(new ChannelInitializer<NioSocketChannel>() { // 3
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder()); // 5
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() { // 6
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                .bind(6666); // 4

    }
}
```

代码解读：

1. 创建 NioEventLoopGroup，可以简单理解为 线程池 + Selector
2. 选择服务器 Socket 实现类，其中 NioServerSocketChannel 表示基于 NIO 的服务器端实现，其他实现还有

   ![图 1](../../.image/b2486990d819129ffa63d91a467e8196c0defae3112e43ed45cd431ee7eb1b1d.png)  
3. 方法叫 childHandler, 是接下来添加的处理器都是给 SocketChannel 用的，而不是给 ServerSocketChannel。ChannelInitializer 处理器（仅执行一次），它的作用是等待客户端 SocketChannel 建立连接后，执行 initChannel 以便添加更多的处理器
4. ServerSocketChannel 绑定的监听端口
5. SocketChannel 的处理器，序列化 ByteBuf => String
6. SocketChannel 的业务处理器，使用上一个处理器的处理结果

### 客户端

```java
new Bootstrap()
    .group(new NioEventLoopGroup()) // 1
    .channel(NioSocketChannel.class) // 2
    .handler(new ChannelInitializer<Channel>() { // 3
        @Override
        protected void initChannel(Channel ch) {
            ch.pipeline().addLast(new StringEncoder()); // 8
        }
    })
    .connect("127.0.0.1", 8080) // 4
    .sync() // 5
    .channel() // 6
    .writeAndFlush(new Date() + ": hello world!"); // 7
```

代码解读：

1. 创建 NioEventLoopGroup，同 Server
2. 选择客户端 Socket 实现类，NioSocketChannel 表示基于 NIO 的客户端实现，其他实现还有

   ![图 2](../../.image/f0545b7fa53dedf4523b0e4b65a5062f2c625ad15a13bdbaf14d492be10fabad.png)  
3. 添加 SocketChannel 的处理器，ChannelInitializer 处理器（仅执行一次），它的作用是待客户端 SocketChannel 建立连接后，执行 initChannel 以便添加更多的处理器
4. 指定要连接的服务器和端口
5. Netty 中很多方法都是异步的，如 connect，这时需要使用 sync 方法等待 connect 建立连接完毕
6. 获取 channel 对象，它即为通道抽象，可以进行数据读写操作
7. 写入消息并清空缓冲区
8. 消息会经过通道 handler 处理，这里即序列化
9. 数据经过网络传输，到达服务器端，服务器端 5 和 6 处的 handler 先后被触发，走完一个流程

#### 💡 提示

> 一开始需要树立正确的数据通道
>
> - 把 channel 理解为数据的通道
> - 把 msg 理解为流动的数据，最开始输入是 ByteBuf，但经过 pipeline 的加工，会变成其他类型对象，最后输出又变成 ByteBuf
> - 把 handler 理解为数据的处理工序
>   - 工序有多道，合在一起就是 pipeline，pipeline 负责发布事件，传播给每个 handler，handler 对自己感兴趣的事件进行处理（重写了相应事件处理方法）
>   - handler 分为 Inbound（入站）和 Outbound（出站）
> 把 EventLoop 理解为处理数据的工人
> - 工人可以管理多个 channel 的 io 操作，并且一旦工人负责了某个 channel，就要负责到底（绑定）
> - 工人既可以执行 io 操作，也可以进行任务处理，每位工人有任务队列，队列里可以堆放多个 channel 待处理任务，任务分为普通任务、定时任务
> - 工人按照 pipeline 顺序，一次按照 handler 的规划处理数据，可以为每道工序执行不同的工人

## 组件

### EventLoop

事件循环对象

EventLoop 本质是一个单线程执行器（同时维护了一个 Selector），里面有 run 方法处理 Channel 上源源不断的 io 事件
它的继承关系比较复杂

- 一条线是继承自 j.u.c.ScheduledExecutorService，因此包含了线程池中所有方法
- 另一条线是继承自 Netty 自己的 OrderedEeventExecutor
  - 提供了 boolean inEventLoop(Thread thread) 方法判断一个线程是否属于此 EventLoop
  - 提供了 parent 方法来查看自己属于哪个 EventLoopGroup

事件循环组

EventLoopGroup 是一组 EventLoop，Channel 一般会调用 EventLoopGroup 的 register 方法来绑定一个 EventLoop，后续这个 Channel 上的 io 事件都由此 EventLoop 来处理（保证了 io 事件处理时的线程安全）

- 继承自 netty 自己的 EventLoopGroup
  - 实现了 Iterable 接口提供 EventLoop 的能力
  - 另有 next 方法获取集合中下一个 EventLoop

以一个简单的实现为例：

```java
// 内部创建了两个 EventLoop, 每个 EventLoop 维护一个线程
DefaultEventLoopGroup group = new DefaultEventLoopGroup(2);
System.out.println(group.next());
System.out.println(group.next());
System.out.println(group.next());
```

也可以使用 for 循环

```java
DefaultEventLoopGroup group = new DefaultEventLoopGroup(2);
for (EventExecutor eventLoop : group) {
    System.out.println(eventLoop);
}
```

#### 💡 优雅关闭

shutdownGracefully 方法。该方法首先切换 EventLoopGroup 到关闭状态从而拒绝新的任务的加入，然后当任务队列中的任务都处理完后，停止线程的执行，从而确保整体应用是在正常有序的状态下退出的

#### 演示 NioEventLoop 处理 io 事件

服务器端一个 boss 两个 worker

```java
public class EventLoopServer {

    public static void main(String[] args) throws InterruptedException {
        new ServerBootstrap()
                // Boss 和 Worker
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.info(buf.toString(StandardCharsets.UTF_8));
                                ctx.fireChannelRead(msg);
                            }
                        }).addLast(new DefaultEventLoopGroup(), "defaultHandler", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.info(buf.toString(StandardCharsets.UTF_8));
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
```

客户端

```java
public class EventLoopClient {

    public static void main(String[] args) throws Exception {
        Channel channel = new Bootstrap()
                .group(new NioEventLoopGroup(1))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress(8080))
                .sync()
                .channel();
        System.out.println(channel);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String str;
        while ((str = reader.readLine()) != null) {
            channel.writeAndFlush(str);
        }
    }
}
```

#### 💡 handler 执行中如何换人

关键代码 `io.netty.channel.AbstractChannelHandlerContext#invokeChannelRead()`

```java
static void invokeChannelRead(final AbstractChannelHandlerContext next, Object msg) {
    final Object m = next.pipeline.touch(ObjectUtil.checkNotNull(msg, "msg"), next);
    // 下一个 handler 的 EventLoop
    EventExecutor executor = next.executor();
    // 如果下一个 handler 的 EventLoop 与当前的 EventLoop 是同一个线程，直接调用
    if (executor.inEventLoop()) {
        next.invokeChannelRead(m);
    }
    // 否则，将要执行的任务提交给下一个 EventLoop（换人）
    else {
        executor.execute(() -> next.invokeChannelRead(m));
    }
}
```

### Channel

channel 的主要作用

- close() 可以用来关闭 channel
- closeFuture() 用来处理 channel 的关闭
  - sync() 方法的作用是同步等待 channel 关闭
  - addListener() 是异步等待 channel 关闭
- pipeline 添加处理器
- write() 将数据写入
- writeAndFlush() 将 数据写入并刷出

#### ChannelFuture

> channel()

获取 channel 对象

由于 connect() 是异步的，因此 ChannelFuture 不能立刻获取 channel 对象，需通过 sync() 或 addListener() 两个方法

> sync()

同步等待连接完成

```java
public class ChannelFutureTest {

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture cf = ChannelFactory.getChannelFuture();
       testSync(cf);
    }

    private static void testSync(ChannelFuture cf) throws InterruptedException {
        Channel channel = cf.sync().channel();
        channel.writeAndFlush("hello");
    }
}
```

> addListener()

添加 listener，在连接建立时被调用

```java
public class ChannelFutureTest {

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture cf = ChannelFactory.getChannelFuture();
        testListener(cf);
    }

    private static void testListener(ChannelFuture cf) {
        cf.addListener((ChannelFutureListener) future -> {
            Channel channel = future.channel();
            channel.writeAndFlush("hello");
        });
    }
}
```

#### closeFuture

> sync()

同步等待 channel 关闭

```java
public class CloseFutureTest {

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture cf = ChannelFactory.getChannelFuture();
        Channel channel = cf.sync().channel();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String line;
            while ((line = scanner.next()) != null) {
                if (line.equals("q")) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(line);
            }
        }, "input").start();
        ChannelFuture closeFuture = channel.closeFuture();
        testSync(closeFuture);
    }

    private static void testSync(ChannelFuture closeFuture) throws InterruptedException {
        log.info("关闭之前");
        // 主线程会在这里阻塞直到 channel 关闭
        closeFuture.sync();
        log.info("处理关闭之后的操作");
    }
}
```

> addListener()

添加监听器在 channel 关闭时被调用

```java
public class CloseFutureTest {

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture cf = ChannelFactory.getChannelFuture();
        Channel channel = cf.sync().channel();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String line;
            while ((line = scanner.next()) != null) {
                if (line.equals("q")) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(line);
            }
        }, "input").start();
        ChannelFuture closeFuture = channel.closeFuture();
        testListener(closeFuture);
    }

    private static void testListener(ChannelFuture closeFuture) {
        closeFuture.addListener(future -> log.info("处理关闭之后的操作"));
    }

}
```

#### 💡 异步提升的是什么

Netty 异步提升的是吞吐量

### Future & Promise

在异步处理时，经常用到这两个接口

- jdk Future 只能同步等待任务结束（或成功、或失败）才能得到结果
- netty Future 可以同步等待任务结束得到结果，也可以异步方式得到结果，但是要等到任务结束
- netty Promise 不仅有 netty Future 的功能，而且脱离了任务独立存在，只作为两个线程间传递结果的容器

| 功能/名称    | jdk Future                      | netty Future                                                 | Promise      |
| ------------ | ------------------------------ | ------------------------------------------------------------ | ------------ |
| cancel       | 取消任务                        | -                                                            | -            |
| isCanceled   | 任务是否取消                    | -                                                            | -            |
| isDone       | 任务是否完成，不能区分成功失败    | -                                                            | -            |
| get          | 获取任务结果，阻塞等待           | -                                                            | -            |
| getNow       | -                              | 获取任务结果，非阻塞，还未产生结果时返回 null                    | -            |
| await        | -                              | 等待任务结束，如果任务失败，不会抛异常，而是通过 isSuccess 判断   | -            |
| sync         | -                              | 等待任务结束，如果任务失败，抛出异常                             | -            |
| isSuccess    | -                              | 判断任务是否成功                                               | -            |
| cause        | -                              | 获取失败信息，非阻塞，如果没有失败，返回null                     | -            |
| addLinstener | -                              | 添加回调，异步接收结果                                         | -            |
| setSuccess   | -                              | -                                                            | 设置成功结果 |
| setFailure   | -                              | -                                                            | 设置失败结果 |

#### 例1

同步处理任务成功

```java
DefaultEventLoop eventExecutors = new DefaultEventLoop();
DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

eventExecutors.execute(()->{
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    log.debug("set success, {}",10);
    promise.setSuccess(10);
});

log.debug("start...");
log.debug("{}",promise.getNow()); // 还没有结果
log.debug("{}",promise.get());
```

#### 例2

异步处理任务成功

```java
DefaultEventLoop eventExecutors = new DefaultEventLoop();
DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

// 设置回调，异步接收结果
promise.addListener(future -> {
    // 这里的 future 就是上面的 promise
    log.debug("{}",future.getNow());
});

// 等待 1000 后设置成功结果
eventExecutors.execute(()->{
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    log.debug("set success, {}",10);
    promise.setSuccess(10);
});

log.debug("start...");
```

#### 例3

同步处理任务失败 - sync & get

```java
DefaultEventLoop eventExecutors = new DefaultEventLoop();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

        eventExecutors.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RuntimeException e = new RuntimeException("error...");
            log.debug("set failure, {}", e.toString());
            promise.setFailure(e);
        });

        log.debug("start...");
        log.debug("{}", promise.getNow());
        promise.get(); // sync() 也会出现异常，只是 get 会再用 ExecutionException 包一层异常
```

#### 例4

同步处理任务失败 - await

```java
DefaultEventLoop eventExecutors = new DefaultEventLoop();
DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

eventExecutors.execute(() -> {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    RuntimeException e = new RuntimeException("error...");
    log.debug("set failure, {}", e.toString());
    promise.setFailure(e);
});

log.debug("start...");
log.debug("{}", promise.getNow());
promise.await(); // 与 sync 和 get 区别在于，不会抛异常
log.debug("result {}", (promise.isSuccess() ? promise.getNow() : promise.cause()).toString());
```

#### 例5

异步处理任务失败

```java
DefaultEventLoop eventExecutors = new DefaultEventLoop();
DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

promise.addListener(future -> {
    log.debug("result {}", (promise.isSuccess() ? promise.getNow() : promise.cause()).toString());
});

eventExecutors.execute(() -> {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    RuntimeException e = new RuntimeException("error...");
    log.debug("set failure, {}", e.toString());
    promise.setFailure(e);
});

log.debug("start...");
```

### Handler & Pipeline

ChannelHandler 用来处理 Channel 上的各种事件，分为入站、出站两种。所有 ChannelHandler 连成一串，就是 Pipeline

- 入站处理器通常是 ChannelInboundHandlerAdapter 的子类，主要用来读取客户端数据，写回结果
- 出站处理器通常是 ChannelOutboundHandlerAdapter 的子类，主要对写回结果进行加工

打个比喻，每个 Channel 是一个产品的加工车间，Pipeline 是车间中的流水线，ChannelHandler 就是流水线上的各道工序，而后面讲的 ByteBuf 是原材料，经过很多工序的加工：先经过一道道入站工序，再经过一道道出站工序最终变成产品

```java
public class ChannelHandlerTest {

    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(1);
                                        super.channelRead(ctx, msg);
                                    }
                                })
                                .addLast(new ChannelOutboundHandlerAdapter(){
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        System.out.println(7);
                                        super.write(ctx, msg, promise);
                                    }
                                })
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(2);
                                        super.channelRead(ctx, msg);
                                    }
                                })
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(3);
                                        ctx.channel().writeAndFlush(msg);
                                    }
                                })
                                .addLast(new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        System.out.println(4);
                                        super.write(ctx, msg, promise);
                                    }
                                })
                                .addLast(new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        System.out.println(5);
                                        super.write(ctx, msg, promise);
                                    }
                                })
                                .addLast(new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        System.out.println(6);
                                        super.write(ctx, msg, promise);
                                    }
                                });
                    }
                })
                .bind(8080);
    }
}
```

- addLast() 是将 handler 加在队列（head -> h1 -> h2 -> h3 -> tail）的 tail 尾节点之前
- ctx.channel().write() 从尾部开始查找出站处理器
- ctr.write() 是从当前节点往前找上一个出站处理器

EmbeddedChannel

```java
public class EmbeddedChannelTest {

    public static void main(String[] args) {
        ChannelInboundHandlerAdapter h1 = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.info("1");
                super.channelRead(ctx, msg);
            }
        };
        ChannelInboundHandlerAdapter h2 = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.info("2");
                super.channelRead(ctx, msg);
            }
        };
        ChannelOutboundHandlerAdapter h3 = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.info("3");
                super.write(ctx, msg, promise);
            }
        };
        ChannelOutboundHandlerAdapter h4 = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.info("4");
                super.write(ctx, msg, promise);
            }
        };

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(h1, h2, h3, h4);
        // 模拟入站操作
        embeddedChannel.writeInbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("hello".getBytes()));
        // 模拟出站操作
        embeddedChannel.writeOutbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("world".getBytes()));
    }
}
```

### ByteBuf

字节数据的封装

#### 创建

```java
// 默认 capacity 256
ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
log(buffer);
```

其中 log() 方法参考如下

```java
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append(buffer)
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf);
```

#### 直接内存 vs 堆内存

创建池化基于堆的 ByteBuf

```java
ByteBuf buffer = ByteBufAllocator.DEFAULT.heapBuffer(10);
```

创建池化基于直接内存的 ByteBuf

```java
ByteBuf buffer = ByteBufAllocator.DEFAULT.directBuffer(10);
```

- 直接内存创建和销毁的代价昂贵，但读写性能高（少一次内存复制），适合配合池化功能一起用
- 直接内存对 GC 压力小，不受 JVM 垃圾回收的管理，但也要注意及时主动释放

#### 池化 vs 非池化

池化的最大意义在于可以重用 ByteBuf，优点有

- 没有池化，则每次都得创建新的 ByteBuf 实例，这个操作对直接内存代价昂贵，就算是堆内存，也会增加 GC 压力
- 有了池化，则可以重用池中 ByteBuf 实例，并且采用了与 jemalloc 类似的内存分配算法提升分配效率
- 池化功能是否开启，可以通过系统环境变量来设定

```java
-Dio.netty.allocator.type={unpooled|pooled}
```

- 4.1 之后，非 Android 平台默认启用池化实现，Android 默认关闭
- 4.1 之前，池化功能还不成熟，默认是非池化实现

