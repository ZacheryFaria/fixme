package zfaria.fixme.broker;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import zfaria.fixme.core.notation.Fix;
import zfaria.fixme.core.notation.FixSerializer;
import zfaria.fixme.core.notation.FixTag;

public class FixSenderHandler extends SimpleChannelInboundHandler {

    private String destination;

    public FixSenderHandler(String marketID) {
        this.destination = marketID;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Connected");
        Fix f = new Fix(FixTag.MSG_NEW_ORDER);
        f.addTag(new FixTag(FixTag.ROUTING_RECEIVER_ID, destination));
        byte[] buff = f.serialize();

        ctx.writeAndFlush(Unpooled.wrappedBuffer(buff));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        Fix f = FixSerializer.deserialize(o);
        System.out.println(f.toString());
    }
}
