package github.cheneykwok.demo;

import github.cheneykwok.config.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 消费者启动类
 *
 * @author gzc
 * @date 2023-05-25
 */
public class ConsumerFastStart {
    private static final String TOPIC = "topic-demo";
    private static final String GROUP_ID = "group-demo";

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("bootstrap.servers", KafkaConfig.BROKER_LIST);
        // 设置消费组名称
        properties.put("group.id", GROUP_ID);
        // 创建一个消费组客户端实例
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        // 订阅主题
        consumer.subscribe(Collections.singletonList(TOPIC));
        Map<String, List<PartitionInfo>> listMap = consumer.listTopics();
        for (Map.Entry<String, List<PartitionInfo>> listEntry : listMap.entrySet()) {
            System.out.println(listEntry.getKey());
        }
        // 循环消费消息
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                System.out.print(record.value());
            }
        }
    }

}
