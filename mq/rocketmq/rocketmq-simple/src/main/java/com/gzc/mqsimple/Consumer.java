package com.gzc.mqsimple;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * 消费者
 */
public class Consumer {

    public static void main(String[] args) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("DefaultGroup");
        // 从哪儿接受消息
        consumer.setNamesrvAddr("1.15.156.232:9876");
        // 监听哪个消息队列
//        consumer.subscribe("topic1", "*");
        consumer.subscribe("topic1", "tag1");
        // 需要在 broker.conf 开启配置 enableProperty=true
        consumer.subscribe("topic1", MessageSelector.bySql("age > 17"));
        // 广播模式， 每个 group 的所有 consumer 都会收到所有消息
//        consumer.setMessageModel(MessageModel.BROADCASTING);
        // 负载均衡模式
        consumer.setMessageModel(MessageModel.CLUSTERING);
        // 处理业务流程 注册监听器
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                System.out.println(new String(msg.getBody()));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
    }
}
