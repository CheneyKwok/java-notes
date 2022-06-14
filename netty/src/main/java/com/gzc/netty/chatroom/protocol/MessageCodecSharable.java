package com.gzc.netty.chatroom.protocol;

import com.gzc.netty.chatroom.config.Config;
import com.gzc.netty.chatroom.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * 消息编解码器
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList){
        try {
            ByteBuf out = ctx.alloc().buffer();
            // 4 字节魔数
            out.writeBytes(new byte[]{1, 2, 3, 4});
            // 1 字节版本
            out.writeByte(1);
            // 1 字节的序列化方法 0 jdk 1 json
            out.writeByte(Config.getSerializerAlgorithm().ordinal());
            // 1 字节消息类型
            out.writeByte(msg.getMessageType());
            // 4 字节请求序号

            out.writeInt(msg.getSequenceId());
            // 1 字节 对齐填充用 无意义
            out.writeByte(0xff);
            byte[] body = Config.getSerializerAlgorithm().serialize(msg);
            // 4 字节消息长度
            out.writeInt(body.length);
            // 写入消息体
            out.writeBytes(body);
            outList.add(out);
        } catch (Exception e) {
            log.error("encode error", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte msgType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] body = new byte[length];
        in.readBytes(body);
        final Serializer.Algorithm serializer = Serializer.Algorithm.values()[serializerType];
        final Class<? extends Message> messageClass = Message.getMessageClass(msgType);
        final Message message = serializer.deserialize(messageClass, body);
        out.add(message);
    }

}
