package com.gzc.netty.chatroom.config;

import com.gzc.netty.chatroom.protocol.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public abstract class Config {

    static Properties properties;

    static {
        try {
            InputStream in = Config.class.getResourceAsStream("/application.properties");
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Properties getProperties() {
        return properties;
    }

    public static int getServerPort() {
        String value = properties.getProperty("server.port");
        return Objects.isNull(value) ? 8080 : Integer.parseInt(value);
    }

    public static Serializer.Algorithm getSerializerAlgorithm() {
        String value = properties.getProperty("serializer.algorithm");
        return Objects.isNull(value) ? Serializer.Algorithm.Java : Serializer.Algorithm.valueOf(value);
    }
}
