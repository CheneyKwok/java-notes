package com.gzc.netty.chatroom.protocol;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

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
        },

        Json {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] target) {
                String string = new String(target, StandardCharsets.UTF_8);
                return new Gson().fromJson(string, clazz);
            }

            @Override
            public <T> byte[] serialize(T target) {

                return new Gson().toJson(target).getBytes(StandardCharsets.UTF_8);
            }
        }

    }

}
