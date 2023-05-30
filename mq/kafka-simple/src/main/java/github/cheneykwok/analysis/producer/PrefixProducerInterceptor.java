package github.cheneykwok.analysis.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * 前缀生产者拦截器
 *
 * @author gzc
 * @date 2023-05-26
 */
@Slf4j
public class PrefixProducerInterceptor implements ProducerInterceptor<String, Company> {

    private volatile int sendSuccess = 0;
    private volatile int sendFailure = 0;
    private final String prefix = "prefix-";

    @Override
    public ProducerRecord<String, Company> onSend(ProducerRecord<String, Company> record) {
        Company company = record.value();
        company.setName(prefix + company.getName());
        company.setAddress(prefix + company.getAddress());

        return new ProducerRecord<>(record.topic(), record.partition(), record.timestamp(), record.key(), company, record.headers());
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception == null) {
            sendSuccess++;
        } else {
            sendFailure++;
        }
    }

    @Override
    public void close() {
        double successRatio = (double) sendSuccess / (sendSuccess + sendFailure);
        log.info("发送成功率 = {}%", successRatio * 100);
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
