package com.gzc.netty.chatroom.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 用于扩展序列化、反序列化算法
 */
public interface Serializer {

    /**
     * 反序列化
     */
    <T> T deserialize(Class<T> clazz, byte[] target);

    /**
     * 序列化
     */
    <T> byte[] serialize(T target);

    enum Algorithm implements Serializer {

        Java {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] target) {
                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(target))){
                    return clazz.cast(ois.readObject());
                } catch (Exception e) {
                    throw new RuntimeException("反序列化失败", e);
                }
            }

            @Override
            public <T> byte[] serialize(T target) {
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                     ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                    oos.writeObject(target);
                    return bos.toByteArray();
                } catch (Exception e) {
                    throw new RuntimeException("序列化失败", e);
                }
            }
        }

    }

}
