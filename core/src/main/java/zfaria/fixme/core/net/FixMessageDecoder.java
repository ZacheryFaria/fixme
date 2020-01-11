package zfaria.fixme.core.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import zfaria.fixme.core.fix.Fix;

import java.util.List;

public class FixMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        byte len = buf.getByte(0);
        if (buf.readableBytes() < len + 1) {
            return;
        }

        len = buf.readByte();
        byte[] buff = new byte[len];
        buf.readBytes(buff);

        Fix f = new Fix(buff);
        out.add(f);
    }
}
