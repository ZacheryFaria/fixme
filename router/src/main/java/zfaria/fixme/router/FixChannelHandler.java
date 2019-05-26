package zfaria.fixme.router;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import zfaria.fixme.core.notation.Fix;
import zfaria.fixme.core.notation.FixSerializer;
import zfaria.fixme.core.notation.FixTag;

import java.util.HashMap;
import java.util.Map;

public class FixChannelHandler extends ChannelInboundHandlerAdapter {

    private static int marketID = 100000;
    private static int brokerID = 200000;

    private static Map<Integer, ChannelHandlerContext> connections;

    private boolean broker;

    static {
        connections = new HashMap<>();
    }

    public FixChannelHandler(int brokerPort) {
        broker = brokerPort == 5000;
    }

    private ChannelHandlerContext getContext(Fix f) {
        String deststr = f.getTag(FixTag.ROUTING_RECEIVER_ID).getValue();
        int val = Integer.parseInt(deststr);
        return connections.get(val);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Fix f = FixSerializer.deserialize(msg);
        System.out.println(f.toString());
        ChannelHandlerContext out = getContext(f);
        out.write(Unpooled.copiedBuffer(f.serialize()));
        out.flush();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        int id = broker ? brokerID++ : marketID++;
        Fix fix = new Fix(FixTag.MSG_CONNECT);
        fix.addTag(new FixTag(FixTag.CONNECT_ID, id + ""));
        ctx.write(Unpooled.copiedBuffer(fix.serialize()));
        connections.put(id, ctx);
        id++;
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
