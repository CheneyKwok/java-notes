package github.cheneykwok.analysis.producer;

import github.cheneykwok.analysis.consumer.CompanyDeserializer;
import github.cheneykwok.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

/**
 * kafka生产者分析
 *
 * @author gzc
 * @date 2023-05-26
 */
@Slf4j
public class KafkaProducerAnalysis {

    private static final String topic = "topic-analysis";

    public static Properties initConfig() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.BROKER_LIST);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, CompanySerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CompanySerializer.class.getName());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "producer.client.id.analysis");
        props.put(ProducerConfig.RETRIES_CONFIG, 2);
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DemoPartitioner.class);
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, PrefixProducerInterceptor.class);
        return props;
    }

    public static void main(String[] args) {
        Properties props = initConfig();
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, "hello, kafka!");
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.info("send failed, {}", exception.getMessage());
                }
                if (metadata != null) {
                    log.info("topic: {}, partition: {}, offset: {}", metadata.topic(), metadata.partition(), metadata.offset());
                }

            });
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }


}
