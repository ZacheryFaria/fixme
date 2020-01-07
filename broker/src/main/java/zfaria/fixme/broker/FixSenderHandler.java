package zfaria.fixme.broker;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import zfaria.fixme.core.notation.Fix;
import zfaria.fixme.core.notation.FixSerializer;

public class FixSenderHandler extends SimpleChannelInboundHandler {

    private String destination;

    public FixSenderHandler(String marketID) {
        this.destination = marketID;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Connected");
        Fix f = new Fix(Fix.MSG_NEW_ORDER);
        f.addTag(Fix.DESTINATION_ID, destination);
        byte[] buff = f.serialize();

        ctx.writeAndFlush(Unpooled.wrappedBuffer(buff));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        Fix f = FixSerializer.deserialize(o);
        System.out.println(f.toString());
    }
}
