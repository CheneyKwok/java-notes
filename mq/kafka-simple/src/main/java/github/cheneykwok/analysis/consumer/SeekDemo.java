package github.cheneykwok.analysis.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * seek() 从特定的位移开始拉取消息
 *
 * @author gzc
 * @date 2023-06-12
 */
@Slf4j
public class SeekDemo {

    public static void main(String[] args) {
        try(KafkaConsumer<String, Object> consumer = new KafkaConsumer<>(KafkaConsumerFactory.initConfig())) {
            consumer.subscribe(Arrays.asList(KafkaConsumerFactory.TOPIC));
            consumer.poll(Duration.ofMillis(10000));
            Set<TopicPartition> partitionSet = new HashSet<>();
            while (partitionSet.size() == 0) {
                partitionSet = consumer.assignment();
            }
            for (TopicPartition tp : partitionSet) {
                consumer.seek(tp, 10);
            }
            ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, Object> record : records) {
                log.info(record.toString());
            }
        }

    }
}
