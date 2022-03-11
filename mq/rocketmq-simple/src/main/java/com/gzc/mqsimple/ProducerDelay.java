package com.gzc.mqsimple;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

public class ProducerDelay {

    public static void main(String[] args) throws Exception {
        // 谁来发
        DefaultMQProducer producer = new DefaultMQProducer("DefaultGroup");
        // 发给谁
        producer.setNamesrvAddr("1.15.156.232:9876");
        producer.start();
        for (int i = 0; i < 10 ; i++) {
            // 怎么发、发什么
            String s = i + " 这是一则消息";
            Message message = new Message("topic1",  s.getBytes());
            message.setDelayTimeLevel(3);
            producer.sendOneway(message);
            // 发的结果
            Thread.sleep(1000);
        }
        producer.shutdown();
    }
}
