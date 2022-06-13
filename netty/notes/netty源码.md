# Netty 源码

## 启动剖析

```java
// 1.netty 中使用 NioEventLoopGroup ( 简称 nio boss 线程) 来封装线程和 selector
Selector selector = Selector.open(); 

//2.创建 NioServerSocketChannel，同时会初始化它关联的 handler，以及为原生 ssc 存储 config
NioServerSocketChannel attachment = new NioServerSocketChannel();

//3.创建 NioServerSocketChannel 时，创建了 java 原生的 ServerSocketChannel
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open(); 
serverSocketChannel.configureBlocking(false);

//4.启动 nio boss 线程执行接下来的操作

//5.注册 (仅关联 selector 和 NioServerSocketChannel)，未关注事件
SelectionKey selectionKey = serverSocketChannel.register(selector, 0, attachment);

//6. head -> 初始化器 -> ServerBootstrapAcceptor -> tail，初始化器是一次性的，只为添加 acceptor

//7.绑定端口
serverSocketChannel.bind(new InetSocketAddress(8080));

//8.触发 channel active 事件，在 head 中关注 op_accept 事件
selectionKey.interestOps(SelectionKey.OP_ACCEPT);
```

入口 `io.netty.bootstrap.ServerBootstrap#bind`

关键代码 `io.netty.bootstrap.AbstractBootstrap#doBind`

```java
private ChannelFuture doBind(final SocketAddress localAddress) {
    // 1.执行初始化和注册 regFuture，会由 initAndRegister 设置其是否完成，从而回调 3.2 处代码
    final ChannelFuture regFuture = initAndRegister();
    final Channel channel = regFuture.channel();
    if (regFuture.cause() != null) {
        return regFuture;
    }
    // 2. 因为是 initAndRegister 异步执行，需要分两种情况来看，调试也需要通过 suspend 断电类型加以区分
    // 2.1 如果已经完成
    if (regFuture.isDone()) {
        // At this point we know that the registration was complete and successf
        ChannelPromise promise = channel.newPromise();
        // 3.1 立刻调用 doBind0
        doBind0(regFuture, channel, localAddress, promise);
        return promise;
    } else {
        // Registration future is almost always fulfilled already, but just in c
        final PendingRegistrationPromise promise = new PendingRegistrationPromis
        // 3.2 回调 doBind0
        regFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception
                Throwable cause = future.cause();
                if (cause != null) {
                    // 处理异常
                    promise.setFailure(cause);
                } else {
                    // 3.由注册线程去执行 doBind0
                    promise.registered();
                    doBind0(regFuture, channel, localAddress, promise);
                }
            }
        });
        return promise;
    }
}
```

1 关键代码 `io.netty.bootstrap.AbstractBootstrap#initAndRegister`

```java
final ChannelFuture initAndRegister() {
    Channel channel = null;
    try {
        // 创建 NioServerSocketChannel 和 ServerSocketChannel
        channel = channelFactory.newChannel();
        // 1.1 初始化 - 做的事就是添加一个初始化器 ChannelInitializer
        init(channel);
    } catch (Throwable t) {
        if (channel != null) {
            channel.unsafe().closeForcibly();
            return new DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE).setFailure(t);
        }
        return new DefaultChannelPromise(new FailedChannel(), GlobalEventExecutor.INSTANCE).setFailure(t);
    }
    // 1.2 注册 - 做的事就是将原生 channel 注册到 selector 上
    ChannelFuture regFuture = config().group().register(channel);
    if (regFuture.cause() != null) {
        if (channel.isRegistered()) {
            channel.close();
        } else {
            channel.unsafe().closeForcibly();
        }
    }

    return regFuture;
}
```

1.1 关键代码 `io.netty.bootstrap.ServerBootstrap#init`

```java
void init(Channel channel) {
    setChannelOptions(channel, newOptionsArray(), logger);
    setAttributes(channel, newAttributesArray());
    ChannelPipeline p = channel.pipeline();
    final EventLoopGroup currentChildGroup = childGroup;
    final ChannelHandler currentChildHandler = childHandler;
    final Entry<ChannelOption<?>, Object>[] currentChildOptions = newOptionsArray(childOptions);
    final Entry<AttributeKey<?>, Object>[] currentChildAttrs = newAttributesArray(childAttrs);
    // 为 NioServerSocketChannel 添加初始化器
    p.addLast(new ChannelInitializer<Channel>() {
        @Override
        public void initChannel(final Channel ch) {
            final ChannelPipeline pipeline = ch.pipeline();
            ChannelHandler handler = config.handler();
            if (handler != null) {
                pipeline.addLast(handler);
            }
            // 初始化器的职责就是将 ServerBootstrapAcceptor 加入至 NioServerSocketChannel
            // ServerBootstrapAcceptor 是一个handler，作用是 accept 事件后建立连接
            ch.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    pipeline.addLast(new ServerBootstrapAcceptor(
                            ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
                }
            });
        }
    });
}
```

1.2 关键代码 `io.netty.channel.AbstractChannel.AbstractUnsafe#register`

```java
public final void register(EventLoop eventLoop, final ChannelPromise promise) {
    // 一些检查，略 ......


    AbstractChannel.this.eventLoop = eventLoop;
    if (eventLoop.inEventLoop()) {
        register0(promise);
    } else {
        try {
            // 首次执行 execute 方法时，会启动 nio 线程，之后注册等操作在 nio 线程上执行
            // 因为只有一个 NioServerSocketChannel, 因此也就只会有一个 boss nio 线程
            eventLoop.execute(new Runnable() {
                @Override
                public void run() {
                    register0(promise);
                }
            });
        } catch (Throwable t) {
            logger.warn(
                    "Force-closing a channel whose registration task was not accepted by an event loop: {}",
                    AbstractChannel.this, t);
            closeForcibly();
            closeFuture.setClosed();
            safeSetFailure(promise, t);
        }
    }
}
```

register0 `io.netty.channel.AbstractChannel.AbstractUnsafe#register0`

```java
private void register0(ChannelPromise promise) {
    try {

        if (!promise.setUncancellable() || !ensureOpen(promise)) {
            return;
        }
    
        boolean firstRegistration = neverRegistered;
        // 1.2.1 原生的 nio channel绑定到 selecotr 上，注意此时没有注册 selecotr 关注事件，附件为 NioServerSocketChannel
        doRegister();
        neverRegistered = false;
        registered = true;
        // 1.2.2 执行 NioServerSocketChannel 初始化器的 initChannel
        pipeline.invokeHandlerAddedIfNeeded();
        // 回调 3.2 io.netty.bootstrap.AbstractBootstrap#doBind0
        safeSetSuccess(promise);
        pipeline.fireChannelRegistered();

        // 对应 server socket channel 还未绑定，isActive 为 false
        if (isActive()) {
            if (firstRegistration) {
                pipeline.fireChannelActive();
            } else if (config().isAutoRead()) {

                beginRead();
            }
        }
    } catch (Throwable t) {

        closeForcibly();
        closeFuture.setClosed();
        safeSetFailure(promise, t);
    }
}
```

doBind0 `io.netty.bootstrap.AbstractBootstrap#doBind0`

```java
private static void doBind0(
        final ChannelFuture regFuture, final Channel channel,
        final SocketAddress localAddress, final ChannelPromise promise) {
    // This method is invoked before channelRegistered() is triggered.  Give user handlers a chance to set up
    // the pipeline in its channelRegistered() implementation.
    channel.eventLoop().execute(new Runnable() {
        @Override
        public void run() {
            if (regFuture.isSuccess()) {
                channel.bind(localAddress, promise).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                promise.setFailure(regFuture.cause());
            }
        }
    });
}
```

最终调用 `io.netty.channel.AbstractChannel.AbstractUnsafe#bind` 的 doBind()

```java
public final void bind(final SocketAddress localAddress, final ChannelPromise promise) {
    assertEventLoop();
    if (!promise.setUncancellable() || !ensureOpen(promise)) {
        return;
    }
    // See: https://github.com/netty/netty/issues/576
    if (Boolean.TRUE.equals(config().getOption(ChannelOption.SO_BROADCAST)) &&
        localAddress instanceof InetSocketAddress &&
        !((InetSocketAddress) localAddress).getAddress().isAnyLocalAddress() &&
        !PlatformDependent.isWindows() && !PlatformDependent.maybeSuperUser()) {
        // Warn a user about the fact that a non-root user can't receive a
        // broadcast packet on *nix if the socket is bound on non-wildcard address.
        logger.warn(
                "A non-root user can't receive a broadcast packet if the socket " +
                "is not bound to a wildcard address; binding to a non-wildcard " +
                "address (" + localAddress + ") anyway as requested.");
    }
    boolean wasActive = isActive();
    try {
        // 3.3 执行端口绑定
        doBind(localAddress);
    } catch (Throwable t) {
        safeSetFailure(promise, t);
        closeIfClosed();
        return;
    }
    if (!wasActive && isActive()) {
        invokeLater(new Runnable() {
            @Override
            public void run() {
                // 3.4 触发 active 事件
                pipeline.fireChannelActive();
            }
        });
    }
    safeSetSuccess(promise);
}
```

3.3 doBind() `io.netty.channel.socket.nio.NioServerSocketChannel#doBind`

```java
protected void doBind(SocketAddress localAddress) throws Exception {
    if (PlatformDependent.javaVersion() >= 7) {
        javaChannel().bind(localAddress, config.getBacklog());
    } else {
        javaChannel().socket().bind(localAddress, config.getBacklog());
    }
}
```

3.4 关键代码 `io.netty.channel.DefaultChannelPipeline.HeadContext#channelActive`

```java
public void channelActive(ChannelHandlerContext ctx) {
    ctx.fireChannelActive();
    // 触发 read (NioServerSocketChannel 上的 read 不是读取数据，只是为了触发 channel 的事件注册)
    readIfIsAutoRead();
}
```

关键代码 `io.netty.channel.nio.AbstractNioChannel#doBeginRead`

```java
protected void doBeginRead() throws Exception {
    // Channel.read() or ChannelHandlerContext.read() was called
    final SelectionKey selectionKey = this.selectionKey;
    if (!selectionKey.isValid()) {
        return;
    }

    readPending = true;

    final int interestOps = selectionKey.interestOps();
    // readInterestOp 取值是 16，在 NioServerSocketChannel 创建时初始化好，代表关注 accept 事件
    if ((interestOps & readInterestOp) == 0) {
        selectionKey.interestOps(interestOps | readInterestOp);
    }
}
```

## EventLoop剖析

### NioEventLoopGroup 的重要组成

selector、线程、任务队列，NioEventLoopGroup 既会处理 IO 事件，也会处理普通任务和定时任务

```java
private Selector selector;          // netty 对原生 selecotr 包装后的 selecotr
private Selector unwrappedSelector; // nio 原生 selecotr

private final Queue<Runnable> taskQueue;
private volatile Thread thread;

PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue; // 处理定时任务用
```

### selecotr 何时创建

在构造方法时被创建

```java
NioEventLoop() {
        ...
        final SelectorTuple selectorTuple = openSelector();
        this.selector = selectorTuple.selector;
        this.unwrappedSelector = selectorTuple.unwrappedSelector;
    }

private SelectorTuple openSelector() {
       final Selector unwrappedSelector;
        try {
            unwrappedSelector = provider.openSelector();
        } catch (IOException e) {
            throw new ChannelException("failed to open a new selector", e);
        }
        ....
}
```

#### selecotr 为何有两个 selecotr 成员

unwrappedSelector 为 nio 原生 selector，netty 对其进行了封装，将 set 集合中 selectedKeys 放入了数组中，提高了遍历的性能

### eventLoop 中的 nio 线程在何时启动

- 当首次调用 execute() 时，会将当前 executor 执行器线程赋值给 nio 的 thread

- 通过 state 状态位控制线程只会启动一次

```java
    private void execute(Runnable task, boolean immediate) {
        boolean inEventLoop = inEventLoop();
        addTask(task);
        if (!inEventLoop) {
            startThread();
            ...
    }

    private void startThread() {
        if (state == ST_NOT_STARTED) {
            if (STATE_UPDATER.compareAndSet(this, ST_NOT_STARTED, ST_STARTED)) {
                boolean success = false;
                try {
                    doStartThread();
                    success = true;
                } finally {
                    if (!success) {
                        STATE_UPDATER.compareAndSet(this, ST_STARTED, ST_NOT_STARTED);
                    }
                }
            }
        }
    }

private void doStartThread() {
    assert thread == null;
    executor.execute(new Runnable() {
        @Override
        public void run() {
            thread = Thread.currentThread();
            ...
    
```

### 提交普通任务会不会结束 select 阻塞

会调用 wakeup 唤醒 selecotr，结束 select 阻塞

```java
private void execute(Runnable task, boolean immediate) {
    boolean inEventLoop = inEventLoop();
    addTask(task);
    if (!inEventLoop) {
        startThread();
        if (isShutdown()) {
            boolean reject = false;
            try {
                if (removeTask(task)) {
                    reject = true;
                }
            } catch (UnsupportedOperationException e) {

            }
            if (reject) {
                reject();
            }
        }
    }
    if (!addTaskWakesUp && immediate) {
        wakeup(inEventLoop);
    }
}

// io.netty.channel.nio.NioEventLoop#wakeup

protected void wakeup(boolean inEventLoop) {
    if (!inEventLoop && nextWakeupNanos.getAndSet(AWAKE) != AWAKE) {
        selector.wakeup();
    }
}
```

### wakeup 方法中的代码如何理解， nextWakeupNanos 变量的作用是什么

EventLoop 线程启动后，以 for 死循环的方式处理 IO 事件及任务队列中的任务。其中一个问题是，如何管理任务队列中任务堆积的问题，即如果一直阻塞在 IO 上，任务可能永远得不到执行

- lazyExecute(Runnable task)
  
  该方法不会去唤醒阻塞在 IO 上的线程，即把任务加入到任务队列中即返回

- execute(Runnable task, boolean immediate)
  
  该方法提供了两种机制来判断是否需要立即唤醒

  - 如果 task 类型是 LazyRunnable，则类似 lazyExecute
  - 如果 task 类型不是 LazyRunnable（`execute(task, !(task instanceof LazyRunnable) && wakesUpForTask(task))`），则会调用 wakesUpForTask(task) 返回 true，即立即唤醒阻塞在 IO 上的线程。wakesUpForTask() 可以被重载，用于定制唤醒逻辑，默认返回 true 立即唤醒

唤醒控制流程

```java
// 当用户要求立即唤醒，且添加任务时不能自动唤醒 EventLoop 线程时，执行唤醒逻方法 wakeup()
if (!addTaskWakesUp && immediate) {
    wakeup(inEventLoop);
}
```

addTaskWakesUp 为 boolean 类型变量，在创建 EventLoop 时指定，用于标识 EventLoop 中的线程在添加任务时，是否会自动唤醒（一种典型的场景就是 Java 的阻塞队列，生产者添加任务时，消费者会唤醒获取到任务）

immediate 标识是否需要立即唤醒

唤醒方法：wakeup(boolean inEventLoop)

NioEventLoop 中实现

```java
protected void wakeup(boolean inEventLoop) {
    if (!inEventLoop && nextWakeupNanos.getAndSet(AWAKE) != AWAKE) {
        selector.wakeup();
    }
}

private final AtomicLong nextWakeupNanos = new AtomicLong(AWAKE);
```

nextWakeupNanos 是定义在 EventLoop 类中的一个属性，用于记录 EventLoop 线程需唤醒的时间点，`nextWakeupNanos.set(curDeadlineNanos)`，该时间点为 scheduledTaskQueue 下一个任务的到期时间

所以 nextWakeupNanos 不为 AWAKE(-1) 则表示有任务需要执行，即需要唤醒 EventLoop 线程

### 每次循环时，什么时候会进入 SelectStrategy.SELECT 分支

- 当没有任务时，才会进入 SelectStrategy.SELECT 分支阻塞
- 当有任务时，会调用 selectNow 方法，顺表拿到 IO 事件与任务一起处理了

```java
for (;;) {
    try {
        int strategy;
        try {
            strategy = selectStrategy.calculateStrategy(selectNowSupplier, hasTasks());
            switch (strategy) {
            case SelectStrategy.CONTINUE:
                continue;
            case SelectStrategy.BUSY_WAIT:
                // fall-through to SELECT since the busy-wait is not supported with NIO
            case SelectStrategy.SELECT:
            ...
```

```java

public int calculateStrategy(IntSupplier selectSupplier, boolean hasTasks) throws Exception {
    return hasTasks ? selectSupplier.get() : SelectStrategy.SELECT;
}

private final IntSupplier selectNowSupplier = new IntSupplier() {
    @Override
    public int get() throws Exception {
        return selectNow();
    }
}

int selectNow() throws IOException {
    return selector.selectNow();
}
```

#### 何时会 select 阻塞

没有定时任务时

`io.netty.channel.nio.NioEventLoop#run`

```java
if (!hasTasks()) {
    strategy = select(curDeadlineNanos);
}
```

### nio 空轮询 bug 在哪里体现，如何解决

`io.netty.channel.nio.NioEventLoop#unexpectedSelectorWakeup`

```java
if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
    rebuildSelector();
    return true;
}
```

jdk 在 linux 的 selector，重新创建了一个 selector，替换了旧的 selector

### ioRatio 控制什么，设置为 100 有何作用

ioRatio 控制处理 io 事件所占用的时间比例，例如 ioTime 代表执行 io 事件处理耗费的时间为 8s，运行任务的时间则为 2s

`io.netty.channel.nio.NioEventLoop#run`

```java
if (ioRatio == 100) {
    try {
        if (strategy > 0) {
            processSelectedKeys();
        }
    } finally {
        //  如果 ioRatio 设置为 100，则任务的执行不会超时
        ranTasks = runAllTasks();
    }
} else if (strategy > 0) {
    final long ioStartTime = System.nanoTime();
    try {
        processSelectedKeys();
    } finally {
        // Ensure we always run tasks.
        final long ioTime = System.nanoTime() - ioStartTime;
        ranTasks = runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
    }
} else {
    ranTasks = runAllTasks(0); // This will run the minimum number of tasks
}
```

### 在哪里区分不同事件类型

`io.netty.channel.nio.NioEventLoop#processSelectedKey()`

```java
private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
    final AbstractNioChannel.NioUnsafe unsafe = ch.unsafe();
    ...
    try {
        int readyOps = k.readyOps();
        // We first need to call finishConnect() before try to trigger a read(...) or write(...) as otherwi
        // the NIO JDK channel implementation may throw a NotYetConnectedException.
        if ((readyOps & SelectionKey.OP_CONNECT) != 0) {
            // remove OP_CONNECT as otherwise Selector.select(..) will always return without blocking
            // See https://github.com/netty/netty/issues/924
            int ops = k.interestOps();
            ops &= ~SelectionKey.OP_CONNECT;
            k.interestOps(ops);
            unsafe.finishConnect();
        }
        // Process OP_WRITE first as we may be able to write some queued buffers and so free memory.
        if ((readyOps & SelectionKey.OP_WRITE) != 0) {
            // Call forceFlush which will also take care of clear the OP_WRITE once there is nothing left t
            ch.unsafe().forceFlush();
        }
        // Also check for readOps of 0 to workaround possible JDK bug which may otherwise lead
        // to a spin loop
        if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
            unsafe.read();
        }
    } catch (CancelledKeyException ignored) {
        unsafe.close(unsafe.voidPromise());
    }
}
```

## accept 流程剖析

`io.netty.channel.nio.NioEventLoop#processSelectedKey`

```java
if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0)
    // readyOps = 16
    unsafe.read();
}
```

`io.netty.channel.nio.AbstractNioMessageChannel.NioMessageUnsafe#read`

```java
public void read() {
    assert eventLoop().inEventLoop();
    final ChannelConfig config = config();
    final ChannelPipeline pipeline = pipeline();
    final RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
    allocHandle.reset(config);
    boolean closed = false;
    Throwable exception = null;
    try {
        try {
            do {
                // 处理 accept
                int localRead = doReadMessages(readBuf);
                if (localRead == 0) {
                    break;
                }
                if (localRead < 0) {
                    closed = true;
                    break;
                }
                allocHandle.incMessagesRead(localRead);
            } while (continueReading(allocHandle));
        } catch (Throwable t) {
            exception = t;
        }
        int size = readBuf.size();
        for (int i = 0; i < size; i ++) {
            readPending = false;
            // 将在 ServerSocketChannel 的数据 交给 pipelin 中 handler 处理，这里由 head -> acceptor -> tail 中的 acceptor 处理
            pipeline.fireChannelRead(readBuf.get(i));
        }
        ...
}
```

`io.netty.channel.socket.nio.NioServerSocketChannel#doReadMessages`

```java
protected int doReadMessages(List<Object> buf) throws Exception {
    // accept
    SocketChannel ch = SocketUtils.accept(javaChannel());
    try {
        if (ch != null) {
            // 将 SocketChannel 包装为 NioSocketChannel，并设置 configureBlocking 为 false
            buf.add(new NioSocketChannel(this, ch));
            return 1;
        }
    } catch (Throwable t) {
        try {
            ch.close();
        } catch (Throwable t2) {
        }
    }
    return 0;
}

public static SocketChannel accept(final ServerSocketChannel serverSocketChannel) throws IOException {
    try {
        return AccessController.doPrivileged(new PrivilegedExceptionAction<SocketChannel>() {
            @Override
            public SocketChannel run() throws IOException {
                return serverSocketChannel.accept();
            }
        });
    } catch (PrivilegedActionException e) {
        throw (IOException) e.getCause();
    }
}
```

`io.netty.bootstrap.ServerBootstrap.ServerBootstrapAcceptor#channelRead`

ServerBootstrapAcceptor Handler 来继续处理 accept

```java
public void channelRead(ChannelHandlerContext ctx, Object msg) {
    final Channel child = (Channel) msg;
    child.pipeline().addLast(childHandler);
    setChannelOptions(child, childOptions, logger);
    setAttributes(child, childAttrs);
    try {
        // 注册
        childGroup.register(child).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    forceClose(child, future.cause());
                }
            }
        });
    } catch (Throwable t) {
        forceClose(child, t);
    }
}
```

`io.netty.channel.AbstractChannel.AbstractUnsafe#register`

```java
public final void register(EventLoop eventLoop, final ChannelPromise promise) {
    ...
    AbstractChannel.this.eventLoop = eventLoop;
    if (eventLoop.inEventLoop()) {
        register0(promise);
    } else {
        try {
            // 当前是 ServerSocketChannel 的 eventLoop 线程，而 SocketChannel 需要用的新的 eventLoop 线程
            eventLoop.execute(new Runnable() {
                @Override
                public void run() {
                    register0(promise);
                }
            });
        } catch (Throwable t) {
            closeForcibly();
            closeFuture.setClosed();
            safeSetFailure(promise, t);
        }
    }
}
```

`io.netty.channel.AbstractChannel.AbstractUnsafe#register0`

```java
private void register0(ChannelPromise promise) {
    try {
        if (!promise.setUncancellable() || !ensureOpen(promise)) {
            return;
        }
        boolean firstRegistration = neverRegistered;
        // 注册 SocketChannel
        doRegister();
        neverRegistered = false;
        registered = true;
        // 触发 SocketChannel 上的初始化事件 initChannel()
        pipeline.invokeHandlerAddedIfNeeded();
        if (isActive()) {
            if (firstRegistration) {
                // 继续传播，关注 read 事件
                pipeline.fireChannelActive();
            } else if (config().isAutoRead()) {
                 beginRead();
        }
}
    }
}
```

`io.netty.channel.nio.AbstractNioChannel#doRegister`

```java
protected void doRegister() throws Exception {
    boolean selected = false;
    for (;;) {
        try {
            // 将 SocketChannel 注册到 selector 上，并绑定 NioSocketChannel
            selectionKey = javaChannel().register(eventLoop().unwrappedSelector(), 0, this);
            return;
        } catch (CancelledKeyException e) {
            if (!selected) {
                eventLoop().selectNow();
                selected = true;
            } else {
                throw e;
            }
        }
    }
}
```

`io.netty.channel.DefaultChannelPipeline.HeadContext#readIfIsAutoRead`

此时为 SocketChannel 的 head Handler

```java
private void readIfIsAutoRead() {
    if (channel.config().isAutoRead()) {
        // 处理读事件
        channel.read();
    }
}
```

`io.netty.channel.nio.AbstractNioChannel#doBeginRead`

```java
protected void doBeginRead() throws Exception {
    // Channel.read() or ChannelHandlerContext.read() was called
    final SelectionKey selectionKey = this.selectionKey;
    if (!selectionKey.isValid()) {
        return;
    }
    readPending = true;
    final int interestOps = selectionKey.interestOps();
    if ((interestOps & readInterestOp) == 0) {
        // 注册读事件 accept 流程结束
        selectionKey.interestOps(interestOps | readInterestOp);
    }
}
```

## read 流程剖析

`io.netty.channel.nio.NioEventLoop#processSelectedKey`

```java
if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0)
    // readyOps = 1
    unsafe.read();
}
```

`io.netty.channel.nio.AbstractNioByteChannel.NioByteUnsafe#read`

```java
public final void read() {
    final ChannelConfig config = config();
    if (shouldBreakReadReady(config)) {
        clearReadPending();
        return;
    }
    final ChannelPipeline pipeline = pipeline();
    final ByteBufAllocator allocator = config.getAllocator();
    // 动态调整 ByteBuf 大小并强制使用直接内存
    final RecvByteBufAllocator.Handle allocHandle = recvBufAllocHandle();
    allocHandle.reset(config);
    ByteBuf byteBuf = null;
    boolean close = false;
    try {
        do {
            byteBuf = allocHandle.allocate(allocator);
            allocHandle.lastBytesRead(doReadBytes(byteBuf));
            if (allocHandle.lastBytesRead() <= 0) {
                // nothing was read. release the buffer.
                byteBuf.release();
                byteBuf = null;
                close = allocHandle.lastBytesRead() < 0;
                if (close) {
                    // There is nothing left to read as we received an EOF.
                    readPending = false;
                }
                break;
            }
            allocHandle.incMessagesRead(1);
            readPending = false;
            // 交给流水线处理读事件
            pipeline.fireChannelRead(byteBuf);
            byteBuf = null;
        } while (allocHandle.continueReading());
        allocHandle.readComplete();
        pipeline.fireChannelReadComplete();
        if (close) {
            closeOnRead(pipeline);
        }
    } catch (Throwable t) {
        handleReadException(pipeline, byteBuf, t, close, allocHandle);
    } finally {
        if (!readPending && !config.isAutoRead()) {
            removeReadOp();
        }
    }
}
```
