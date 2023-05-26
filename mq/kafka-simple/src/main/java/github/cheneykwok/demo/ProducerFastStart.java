package github.cheneykwok.demo;

import github.cheneykwok.config.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * 生产者启动类
 *
 * @author gzc
 * @date 2023-05-25
 */
public class ProducerFastStart {

    private static final String TOPIC = "topic-demo";

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("bootstrap.servers", KafkaConfig.BROKER_LIST);
        // 配置生产者参数
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "hello, Kafka!");
            // 发送消息
            Future<RecordMetadata> future = producer.send(record);
            System.out.printf("已发送");
//            RecordMetadata recordMetadata = future.get();
//            System.out.println(recordMetadata.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 关闭生产者客户端

    }
}
