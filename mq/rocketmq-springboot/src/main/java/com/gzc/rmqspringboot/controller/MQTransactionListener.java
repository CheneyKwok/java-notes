package com.gzc.rmqspringboot.controller;

import com.gzc.rmqspringboot.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;

@Slf4j
@RocketMQTransactionListener
public class MQTransactionListener implements RocketMQLocalTransactionListener
{
    /**
     * 如果执行失败，可明确返回 ROLLBACK
     * 如果执行成功，建议 返回 UNKNOWN，可能发生断电等情况
     * @param msg
     * @param arg
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        log.info("execute...");
        try {
            log.info("执行数据库操作");
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        // 此时事务还没有提交
        return RocketMQLocalTransactionState.UNKNOWN;
    }

    /**
     * 事务状态回查
     * 如果明确知道事务成功，返回 COMMIT
     * 如果不能明确得知本地事务是否成功，应返回 UNKNOWN 等待服务端下一次事务回查(不会立即触发)，
     * 服务端默认回查15次，如果15次都得到 UNKNOWN，则会回滚该消息。
     * @param msg
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        log.info("check...");
        User user = selectOne(msg);
        if (user != null) {
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.COMMIT;
        }
    }

    private User selectOne(Message message) {
        Object payload = message.getPayload();
        return new User();
    }
}
