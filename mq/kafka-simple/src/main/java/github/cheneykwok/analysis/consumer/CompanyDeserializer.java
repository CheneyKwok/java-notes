package github.cheneykwok.analysis.consumer;

import github.cheneykwok.analysis.producer.Company;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 自定义反序列化器
 *
 * @author gzc
 * @date 2023-05-26
 */
@Slf4j
public class CompanyDeserializer implements Deserializer<Company> {

    @Override
    public Company deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        if (data.length < 8) {
            throw new SerializationException("Size of data received by CompanyDeserializer is shorter than expected");
        }
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            int nameLength = buffer.getInt();
            byte[] nameBytes = new byte[nameLength];
            buffer.get(nameBytes);
            int addressLength = buffer.getInt();
            byte[] addressBytes = new byte[addressLength];
            buffer.get(addressBytes);
            String name = new String(nameBytes, StandardCharsets.UTF_8);
            String address = new String(addressBytes, StandardCharsets.UTF_8);
            return new Company(name, address);
        } catch (Exception e) {
            log.error("topic: {} deserialize failed: {}", topic, e.getMessage());
            return null;
//            throw new SerializationException("Error occur when deserializing");
        }

    }
}
