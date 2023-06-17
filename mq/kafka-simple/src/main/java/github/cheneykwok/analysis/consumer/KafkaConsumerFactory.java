package github.cheneykwok.analysis.consumer;

import github.cheneykwok.config.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author gzc
 * @date 2023-06-12
 */
public class KafkaConsumerFactory {

    public static String TOPIC = "topic-analysis";
    public static String GROUP_ID = "group-analysis";
    public static AtomicBoolean IS_RUNNING = new AtomicBoolean(true);

    public static Properties initConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.BROKER_LIST);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer.client.id.analysis");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CompanyDeserializer.class.getName());
        return props;
    }
}
