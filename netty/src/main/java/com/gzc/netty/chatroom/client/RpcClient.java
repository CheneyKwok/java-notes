package com.gzc.netty.chatroom.client;

import com.gzc.netty.chatroom.message.RpcRequestMessage;
import com.gzc.netty.chatroom.protocol.SequenceIdGenerator;
import com.gzc.netty.chatroom.server.handler.RpcResponseMessageHandler;
import com.gzc.netty.chatroom.server.service.HelloService;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.Proxy;

public class RpcClient {

    public static void main(String[] args) {

        HelloService service = getProxyService(HelloService.class);
        service.sayHello("hello, world!");
    }

    private static <T> T getProxyService(Class<T> target) {
        Object proxyInstance = Proxy.newProxyInstance(target.getClassLoader(), new Class[]{target}, (proxy, method, args) -> {
            // 将方法转化为消息对象
            RpcRequestMessage rpcRequestMessage = RpcRequestMessage.builder()
                    .sequenceId(SequenceIdGenerator.nextId())
                    .interfaceName(target.getName())
                    .methodName(method.getName())
                    .parameterTypes(method.getParameterTypes())
                    .parameterValue(args)
                    .returnType(method.getReturnType())
                    .build();
            Channel channel = RpcChanelFactory.getChannel();
            channel.writeAndFlush(rpcRequestMessage);
            DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
            RpcResponseMessageHandler.PROMISES.put(rpcRequestMessage.getSequenceId(), promise);
            // await
//            promise.await();
//            if (promise.isSuccess()) {
//                return promise.getNow();
//            } else {
//                System.out.println(111);
//                throw new RuntimeException(promise.cause());
//            }
            // await 有异常不会抛出，sync 会抛出
            promise.sync();
            return promise.getNow();
        });
        return target.cast(proxyInstance);
    }
}
