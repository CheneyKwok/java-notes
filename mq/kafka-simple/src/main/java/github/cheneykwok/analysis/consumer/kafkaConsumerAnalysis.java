package github.cheneykwok.analysis.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;

/**
 * kafka 消费者分析
 *
 * @author gzc
 * @date 2023-05-30
 */
@Slf4j
public class kafkaConsumerAnalysis {

    public static void main(String[] args) {

//        while (KafkaConsumerFactory.IS_RUNNING.get()) {
            try(KafkaConsumer<String, Object> kafkaConsumer = new KafkaConsumer<>(KafkaConsumerFactory.initConfig())) {
                kafkaConsumer.subscribe(Collections.singleton(KafkaConsumerFactory.TOPIC));
                ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, Object> record : records) {
                    log.info(">>>>>>>>>>>>>>>>>>>>> topic {}, partition {}, offset {}, key {}, value {}",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
//                }

            }
        }

    }
}
