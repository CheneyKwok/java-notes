package com.gzc.rmqspringboot.service;

import com.gzc.rmqspringboot.Constant;
import com.gzc.rmqspringboot.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;


@Slf4j
//@Service
@RocketMQMessageListener(consumerGroup = "demo-consumer", topic = Constant.TOPIC1)
public class DemoConsumer3 implements RocketMQListener<String> {

    @Override
    public void onMessage(String s) {
        log.info("接受到：{}", s);
    }
}
