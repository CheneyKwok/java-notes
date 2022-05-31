package com.gzc.netty.advance.agreement.codec;

import com.gzc.netty.advance.agreement.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageCodecTest {

    public static void main(String[] args) throws Exception {
//        EmbeddedChannel channel = new EmbeddedChannel(new MessageCodec(), new LoggingHandler(LogLevel.DEBUG));
//        // encode
//        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123456");
//        channel.writeOutbound(message);
//
//        //decode
//        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
//        new MessageCodec().encode(null, message, buf);
//        channel.writeInbound(buf);
        byte[] bytes = {2,3,4,5};
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(bytes);
        log.info("{}", buf.readInt());

    }
}
