package com.gzc.rmqspringboot.service;

import com.alibaba.fastjson.JSON;
import com.gzc.rmqspringboot.Constant;
import com.gzc.rmqspringboot.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RocketMQMessageListener(consumerGroup = "demo-consumer",
        topic = Constant.TOPIC3,
        selectorExpression = Constant.TAG1,
        consumeMode = ConsumeMode.ORDERLY)
public class DemoConsumer6 implements RocketMQListener<String> {

    @Override
    public void onMessage(String str) {
        log.info("接受到：{}", JSON.parseObject(str, Order.class));
    }
}
