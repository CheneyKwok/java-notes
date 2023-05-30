package github.cheneykwok.analysis.consumer;

import github.cheneykwok.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * kafka 消费者分析
 *
 * @author gzc
 * @date 2023-05-30
 */
@Slf4j
public class kafkaConsumerAnalysis {

    private static final String TOPIC = "topic-analysis";
    private static final String GROUP_ID = "group-analysis";
    private static final AtomicBoolean IS_RUNNING = new AtomicBoolean(true);

    public static Properties initConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.BROKER_LIST);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer.client.id.analysis");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CompanyDeserializer.class.getName());
        return props;
    }

    public static void main(String[] args) {

        while (IS_RUNNING.get()) {
            try(KafkaConsumer<String, Object> kafkaConsumer = new KafkaConsumer<>(initConfig())) {
                kafkaConsumer.subscribe(Collections.singleton(TOPIC));
                ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, Object> record : records) {
                    log.info(">>>>>>>>>>>>>>>>>>>>> topic {}, partition {}, offset {}, key {}, value {}",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }

            }
        }

    }
}
