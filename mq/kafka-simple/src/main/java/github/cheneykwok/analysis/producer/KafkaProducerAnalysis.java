package github.cheneykwok.analysis.producer;

import github.cheneykwok.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Arrays;
import java.util.Properties;

/**
 * kafka生产者分析
 *
 * @author gzc
 * @date 2023-05-26
 */
@Slf4j
public class KafkaProducerAnalysis {

    private static final String TOPIC = "topic-analysis";

    public static Properties initConfig() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.BROKER_LIST);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CompanySerializer.class.getName());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "producer.client.id.analysis");
        props.put(ProducerConfig.RETRIES_CONFIG, 2);
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DemoPartitioner.class);
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, Arrays.asList(PrefixProducerInterceptor.class));
        return props;
    }

    public static void main(String[] args) {
        Properties props = initConfig();
        try (KafkaProducer<String, Object> producer = new KafkaProducer<>(props)) {
            Company company = new Company("阿里", "杭州");
            ProducerRecord<String, Object> record = new ProducerRecord<>(TOPIC, company);
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
