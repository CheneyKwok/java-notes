package github.cheneykwok.analysis.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 自定义序列化器
 *
 * @author gzc
 * @date 2023-05-26
 */
@Slf4j
public class CompanySerializer implements Serializer<Company> {

    @Override
    public byte[] serialize(String topic, Company data) {
        if (data == null) {
            throw new SerializationException("failed serializer, data is null");
        }
        byte[] name, address;
        try {
            if (data.getName() != null) {
                name = data.getName().getBytes(StandardCharsets.UTF_8);
            } else {
                name = new byte[0];
            }

            if (data.getAddress() != null) {
                address = data.getAddress().getBytes(StandardCharsets.UTF_8);
            } else {
                address = new byte[0];
            }

            ByteBuffer buffer = ByteBuffer.allocate(8 + name.length + address.length);
            buffer.putInt(name.length);
            buffer.put(name);
            buffer.putInt(address.length);
            buffer.put(address);
            return buffer.array();
        } catch (Exception e) {
            log.info("CompanySerializer 异常：{}", e.getMessage());
            return new byte[0];
        }
    }
}
