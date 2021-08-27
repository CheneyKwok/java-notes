# JUC 多线程&并发

## 进程与线程

### 进程

- 程序：由指令和数据组成，但这些指令要运行，数据要读写，就必须将指令加载至CPU，数据加载至内存。
在指令运行过程中还需要用到磁盘、网络等设备。进程就是用来加载指令、管理内存、管理IO的
- 当一个程序被运行，从磁盘加载这个程序的代码至内存，这就开启了一个进程。
- 进程可以视为程序的一个实例。大部分程序可以同时运行多个实例进程(例如记事本、画图、浏览器)，也有程序只能启动一个实例进程(例如网易云音乐)

### 线程

- 一个进程之内可以分为一到多个线程
- 一个线程就是一个指令流，将指令流中的一条条指定以一定的顺序交给 CPU 执行
- Java中， 线程作为最小调度单位，进程作为资源分配的最小单位。在windows中进程是不活动的，只是作为线程的容器


![](../pics/线程概念.png)

### 二者对比

- 进程基本上相互独立的，而线程存在于进程内，是进程的一个子集
- 进程拥有共享的资源，如内存空间等，供其内部的线程共享
- 进程间通信较为复杂
  - 同一台计算机的进程通信称为 IPC (Inter-process communication)
  - 不同计算机之间的进程通信，需要通过网路，并遵守共同的协议，例如 HTTP
- 线程通讯相对简单，因为它们共享进程内的内存，一个例子是多个线程可以访问同一个共享变量
- 线程更轻量，线程上下文切换成本一般上要比进程上下文切换低

## 并行与并发

单核 cpu 下，线程实际还是**串行执行**。操作系统中有一个组件叫做任务调度器，将 cpu 的时间片(Windows 下时间约为15ms)非分给不同的线程使用，
只是由于 cpu 在线程间(时间片很短)的切换非常快，人类的感觉是**同时运行的**。总结一句话为：**微观串行，宏观并行**

一般将这种**线程轮流使用** cpu 的做法叫做**并发**(concurrent)

多核cpu下，每个核(core)都可以调度运行线程，这些时候线程可以是**并行**(parallel)的

引用 Rob Pike的一段描述：

- 并发(concurrent)是同一时间应对(dealing with)多件事情的能力
- 并行(parallel)是同一时间动手做(doing)多件事情的能力

## 创建线程

### 一 、直接使用 Thread

继承 Thread 类并重写其 run 方法

```java
Thread t = new Thread("t1"){
	public void run(){
	// 执行任务
	}
};
// 启动线程
t.start();
```

### 二、使用 Runnable 配合 Thread

构造 Thread 对象并传入一个 Runnable 对象

优点：把【线程】和【任务】分开

- Thread 代表线程
- Runnable 可运行的任务(线程要执行的代码)

```java
Runnable r = new Runnable() {
	public void run(){

	}
}
Thread t = new Thread(r);
t.start
```

#### 原理之 Thread 与 Runnablede 关系

```java
public Thread(Runnable target) {  
    init(null, target, "Thread-" + nextThreadNum(), 0);  
}
```

在 Thread 的构造方法里会将 Runnable 对象作为 target 参数传入 init 方法， 在 init 方法中再将target 赋给实例变量 target

```java
/* What will be run. */
private Runnable target;
```

```java
private void init(ThreadGroup g, Runnable target,...){
	...
	this.target = target;
	...
}
```

在线程启动后调用 run 方法时，如果发现 Runnable 类型的 target 参数不为 null，则调用其 run 方法

```java
public void run() {
	if (target != null) {
		target.run();
	}
}
```

#### 小结

- 方式一是将线程和任务合并在了一起，方式二是将线程和任务分开了
- 方式一为继承方式，方式二为组合方法，组合优先于继承
- 用 Runnable 更容易与线程池等高级 API 配合
- 用 Runnable 让任务类脱离了 Thread 继承体系，更灵活

### 三、使用 Callable、FutureTask 配合 Thread

向 FutureTask 对象中传入 callable 对象 ，再将 FutureTask 对象传入 Thread对象中，可以处理有返回值的情况

```java
FutureTask<Integer> task = new FutureTask<>(() -> {  
	log.debug("running");  
	return 100;  
});  
Thread t = new Thread(task, "t");  
t.start();  
log.debug("res: {}", task.get());

```

## 查看进程线程的方法

### windows

- tasklist 查看进程
- taskkill /f /pid 强制终止进程

### linux

- ps -fe 查看所有进程
- ps -fT -p  [ PID ]  查看某个进程（PID）的所有线程
- top 动态查看所有进程
- top -H -p [ PID ] 动态 查看某个进程（PID）的所有线程

### Java

- jps -l 查看所有 Java 进程
- jstack [ PID ]  查看某个 Java 进程的所有线程状态
- jconsole 来查看某个 Java 进程中线程的运行情况（图形界面）

jconsole 远程监控配置

```linux
java
-Djava.rmi.server.hostname=`ip地址`
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=`连接端口`
-Dcom.sun.management.jmxremote.ssl=是否安全连接
-Dcom.sun.management.jmxremote.authenticate=是否认证 java类
```

## 栈与栈帧

Java Virtual Machine Stacks ( Java 虚拟机栈 )

JVM 由堆、栈、方法区组成，其中栈内存是给线程用的。每个线程启动后，虚拟机就会为其分配一块栈内存。

- 每个栈由多个栈帧( Frame ) 组成，对应着每次方法调用能时所占用的内存
- 每个线程只能有一个活动栈帧吗，对应着当前正在执行的方法

## 线程上下文切换 ( Thread Context Switch )

因为以下一些原因导致 cpu 不再执行当前的线程，转而执行另一个线程的代码

- 线程的 cpu 时间片用完
- 垃圾回收
- 有更高优先级的线程需要运行
- 线程自己调用了 sleep、yield、wait、park、synchronized、lock等方法

当 Context Switch 发生时，需要由操作系统保存当前线程的状态，并恢复另一个线程的状态，Java 中对应的概念就是程序计数器 ( Program Counter Register )，它的作用是记住下一条 jvm 指令的地址，是线程私有的

- 状态包括程序计数器、虚拟机栈中每个栈帧的信息，如局部变量、操作数栈、返回地址等
- Context Switch 频繁发生会影响性能

## 常见方法

| 方法名 | 功能说明 | 注意 |
| :----: | :-----: | :--: |
| start() | 启动一个新线程，在新的线程运行run方法中的代码 | start 方法只是让线程进入就绪，里面的代码不一定立刻就运行 ( cpu 的时间片还没分给它 )。每个线程对象的 start 方法只能调用一次，如果调用了多次会出现 IllegalThreadStateException
| run() | 新线程启动后会调用的方法 | 如果在构造 Thread 对象时传递了 Runnable 参数， 则线程启动后会调用 Runnable 中的 run 方法，否则默认不执行任何操作。但可以创建 Thread 的子类对象来覆盖默认行为
| join() | 等待线程运行结束 |
| join(long n) | 等待线程运行结束，最多等待 n 毫秒|
| getId() | 获取线程长整型的 id | id 唯一
| getName() | 获取线程名
| setName(String) | 修改线程名
| getPriority() | 获取线程优先级
| setPriority(int) | 修改线程优先级 | Java 中规定线程优先级是 1-10 的整数，较大的优先级能提高该线程被 CPU 调度的机率
| getState() | 获取线程状态 | Java 中的线程状态使用 6 个枚举表示， 分别分 NEW、Runnable、BLOCKED、WAITING、TIMED_WAITING、TERMINATED
| isInterrupted() | 判断是否被打断 | 不会清除**打断标记**
| isAlive() | 线程是否存活 (还没有运行完毕 )
| interrupt() | 打断线程 | 如果被打断的线程正在 sleep、wait、join， 会导致打断的线程抛出 InterruptedException， 并清除打断标记；如果打断正在运行的线程，则会设置打断标记；park 的线程被打断，也会清除打断标记
|interrupted() | 判断当前线程是否被打断 | 会清除打断标记
| currentThread() | 获取当前正在执行的线程
| sleep(long n) | 让当前线程休眠 n 毫秒，休眠时让出 cpu 的时间片给其他线程
| yield() | 提示线程调度器让出当前线程对 cpu 的使用 | 主要是为了测试和调试

## sleep 与 yiled

### sleep

- 调用 sleep 会让当前线程从 Running 进入 Timed Waiting 状态 ( **阻塞**)
- 其他线程可以使用 interrupt 方法打断正在睡眠的线程，这时 sleep 方法会抛出InterruptedException
- 睡眠结束后的线程未必会立刻得到执行，cpu 可能在执行其他线程，时间片还没有分给它
- 建议使用 TimeUnit 的 sleep 代替 Thread 的 sleep 来获得更好的可读性

### yield

- 调用 yield 会让当前线程从 Running 进入 Runnable 就绪状态，然后调度其他线程
- 具体的依赖于操作系统的任务调度器

### 二者区别

- yield 虽然会让当前线程进入就绪状态，但是可能会被任务调度器再一次调度，因为任务调度器在分时间片时会考虑就绪状态。而 sleep 是让线程进入阻塞状态，不会再次被调度

## 线程优先级

- 优先级范围

```java

/**
 * The minimum priority that a thread can have.
 */
public final static int MIN_PRIORITY = 1;

/**
 * The default priority that is assigned to a thread.
 */
public final static int NORM_PRIORITY = 5;

/**
 * The maximum priority that a thread can have.
 */
public final static int MAX_PRIORITY = 10;
```

- 线程优先级会提示( hint ) 调度器优先调度该线程，但它仅仅是一个提示，调度器可以忽略
- 如果 cpu 比较忙，那么高优先级的线程会获得更多的时间片，但是 cpu 闲时，优先级几乎没作用
