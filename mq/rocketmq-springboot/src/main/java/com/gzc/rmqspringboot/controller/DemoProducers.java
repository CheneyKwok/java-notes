package com.gzc.rmqspringboot.controller;

import com.alibaba.fastjson.JSON;
import com.gzc.rmqspringboot.Constant;
import com.gzc.rmqspringboot.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.gzc.rmqspringboot.Constant.*;


@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoProducers {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/send")
    public String producerMessage() {
        User user = new User("Tom", "123456");
        // 方式一
        rocketMQTemplate.send(TOPIC1 + ":" + TAG1, MessageBuilder.withPayload(user).build());
        // 方式二
        rocketMQTemplate.convertAndSend(TOPIC1 + ":" + TAG1, user);
        // 异步发送
        rocketMQTemplate.asyncSend(TOPIC1 + ":" + TAG2 , "异步消息",  new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info(sendResult.toString());
            }

            @Override
            public void onException(Throwable throwable) {
                log.info(throwable.getMessage());
            }
        });
        log.info("发送异步消息");
        // 单向发送
        rocketMQTemplate.sendOneWay(TOPIC1 + ":" + TAG2, "单向消息");
        // 延时消息
        rocketMQTemplate.syncSend(TOPIC1 + ":" + TAG1, MessageBuilder.withPayload("延时消息").build(), 2000, 3);
        // 批量
        List<Message> list = new ArrayList<>();
        byte[] body = "批量消息".getBytes();
        list.add(new Message(TOPIC2, TAG2, body));
        list.add(new Message(TOPIC2, TAG2, body));
        list.add(new Message(TOPIC2, TAG2, body));
        rocketMQTemplate.syncSend(TOPIC2 + ":" + TAG2, list);
        return JSON.toJSONString(user);
    }
}
