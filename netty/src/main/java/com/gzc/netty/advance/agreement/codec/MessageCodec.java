package com.gzc.netty.advance.agreement.codec;

import com.gzc.netty.chatroom.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf out) throws Exception {
        // 4 字节魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 1 字节版本
        out.writeByte(1);
        // 1 字节的序列化方法 0 jdk 1 json
        out.writeByte(0);
        // 1 字节消息类型
        out.writeByte(message.getMessageType());
        // 4 字节请求序号
        out.writeInt(message.getSequenceId());
        // 1 字节 对齐填充用
        out.writeByte(0xff);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(message);
        byte[] body = bos.toByteArray();
        // 4 字节消息长度
        out.writeInt(body.length);
        // 写入消息体
        out.writeBytes(body);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte msgType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] body = new byte[length];
        in.readBytes(body);
        if (serializerType == 0) {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(body));
            Message message = (Message) ois.readObject();
            log.info("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, msgType, sequenceId, length);
            log.info("{}", message);
            list.add(message);
        }

    }
}
