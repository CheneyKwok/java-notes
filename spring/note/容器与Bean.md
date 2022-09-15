# 容器与 Bean

## BeanFactory 与 ApplicationContext

![图 1](../../.image/ee7f20148a9dc6d0a885bc25d36f7adbb755d61350b3632c47994971ff9d6940.png)  

BeanFactory 是 ApplicationContext 的父接口

BeanFactory 才是 Spring 的核心容器，主要的 ApplicationContext 的实现都组合了它的功能

- BeanFactory能干点啥
  - 表面上只有 getBean
  - 实际上控制反转、基本的依赖注入、直至 Bean 的生命周期的各种功能，都由它的实现类提供

![图 3](../../.image/eeb34c7e0f39605164546f646d47a3c146f8a43301985ec6dae300a37c5fa31a.png)  

- ApplicationContext
  
ApplicationContext 的扩展功能主要体现在它的四个父接口上

![图 4](../../.image/c98407d245d6b823de6656287d58ed866305ec4773777254b5bf16f1739727f3.png)  

> MessageSource - 用于处理国际化资源
>
> ResourcePatternResolver - 根据通配符匹配资源，解析为 Resource 对象
>
> EnvironmentCapable - 处理环境信息（如读取环境变量、*.yml、*.properties 中的变量）
>
> ApplicationEventPublisher - 事件发布功能

![图 5](../../.image/90668bc3f300da0d9595f8948648eb05bbb9e500e580906df432483b8287d3d2.png)  

![图 6](../../.image/c5714dcad78ed7ef36c2bde10a9a8758a619b7bc8d802ebaea423c73df845997.png)  

![图 7](../../.image/6c78ae6bf62973ed177934a7a6b1b3f1537e2cd7253c3fc366aa2bcc90266882.png)  

![图 8](../../.image/6ec9f8d80270f2380879c792738071629b28d7fe1d160e9a1e04bc75bcd5e4da.png)  

![图 9](../../.image/c5393f2d4f2a4b9cf4f5c26c2808c04e78414640a88794cec61d7f8477d15d29.png)  

BeanFactory 不会做的事

1. 不会主动添加 BeanFactory 后置处理器
2. 不会主动添加 Bean 后置处理器
3. 不会主动初始化单例
4. 不会解析 ${}、#{}
