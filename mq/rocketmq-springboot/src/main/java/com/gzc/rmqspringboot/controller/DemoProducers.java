package com.gzc.rmqspringboot.controller;

import com.alibaba.fastjson.JSON;
import com.gzc.rmqspringboot.domain.Order;
import com.gzc.rmqspringboot.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
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

    /**
     * 顺序发送
     * @return
     */
    @GetMapping("sendOrder")
    public String OrderProducer() {

        List<Order> orderList = new ArrayList<>();
        generate(orderList, 1L, "创建");
        generate(orderList, 1L, "付款");
        generate(orderList, 1L, "推送");
        generate(orderList, 1L, "完成");

        generate(orderList, 2L, "创建");
        generate(orderList, 2L, "付款");
        generate(orderList, 2L, "推送");
        generate(orderList, 2L, "完成");


        generate(orderList, 3L, "创建");
        generate(orderList, 3L, "付款");
        generate(orderList, 3L, "推送");
        generate(orderList, 3L, "完成");


        generate(orderList, 4L, "创建");
        generate(orderList, 4L, "付款");
        generate(orderList, 4L, "推送");
        generate(orderList, 4L, "完成");

//        for (Order order : orderList) {
//            SendResult sendResult = rocketMQTemplate.syncSendOrderly("topic3:tag1", order, order.getOrderId().toString());
//            log.info("sync send" + sendResult.toString());
//        }

        for (Order order : orderList) {
            rocketMQTemplate.asyncSendOrderly("topic3:tag1", order, order.getOrderId().toString(), new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("async send" + sendResult.toString());
                }

                @Override
                public void onException(Throwable throwable) {
                    log.info(throwable.getMessage());

                }
            });
        }
        return "OK";
    }

    private void generate(List<Order> list, Long id, String desc) {
        Order order = new Order(id, desc);
        list.add(order);
    }

    @GetMapping("/sendTransaction")
    public String transactionProducer() {
//        TransactionMQProducer producer = (TransactionMQProducer) rocketMQTemplate.getProducer();
//        producer.setTransactionListener(new TransactionListener() {
//            @Override
//            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
//                log.info("execute...");
//                return LocalTransactionState.COMMIT_MESSAGE;
//            }
//
//            @Override
//            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
//                log.info("execute...");
////                return LocalTransactionState.ROLLBACK_MESSAGE;
//                return LocalTransactionState.COMMIT_MESSAGE;
//            }
//        });
        TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction("topic4:tag1", MessageBuilder.withPayload("事务消息").build(), null);
        log.info("transaction send" + sendResult.toString());
        return "OK";
    }
}
