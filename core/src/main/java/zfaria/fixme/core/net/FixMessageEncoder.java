package zfaria.fixme.core.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import zfaria.fixme.core.fix.Fix;


public class FixMessageEncoder extends MessageToByteEncoder<Fix> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Fix message, ByteBuf buf) throws Exception {
        byte[] buff = message.serialize();

        buf.capacity(buff.length + 1);

        buf.writeByte(buff.length);
        buf.writeBytes(buff);
    }
}
