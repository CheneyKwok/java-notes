package com.gzc.rmqspringboot.service;

import com.gzc.rmqspringboot.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Slf4j
@Service
@RocketMQMessageListener(consumerGroup = "demo-consumer", topic = Constant.TOPIC2, selectorExpression = Constant.TAG2)
public class DemoConsumer5 implements RocketMQListener<List<Message>> {

    @Override
    public void onMessage(List<Message>  list) {
        for (Message message : list) {

            log.info("接受到：{}", new String(message.getBody()));
        }
    }
}
