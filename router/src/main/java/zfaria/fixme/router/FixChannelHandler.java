package zfaria.fixme.router;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import zfaria.fixme.core.notation.Fix;
import zfaria.fixme.core.notation.FixSerializer;

import java.util.HashMap;
import java.util.Map;

public class FixChannelHandler extends ChannelInboundHandlerAdapter {

    private static int marketID = 100000;
    private static int brokerID = 200000;

    private static Map<Integer, ChannelHandlerContext> connections = new HashMap<>();

    private boolean isBroker;

    public FixChannelHandler(int port) {
        isBroker = port == 5000;
    }

    private ChannelHandlerContext getDestinationContext(Fix f) {
        String deststr = f.getTag(Fix.DESTINATION_ID);
        int val = Integer.parseInt(deststr);
        return connections.get(val);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Fix f = FixSerializer.deserialize(msg);
        System.out.println(f.toString());
        ChannelHandlerContext out = getDestinationContext(f);
        out.write(Unpooled.copiedBuffer(f.serialize()));
        out.flush();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        int id = isBroker ? brokerID++ : marketID++;
        Fix fix = new Fix(Fix.MSG_CONNECT);
        fix.addTag(Fix.CONNECT_ID, Integer.toString(id));
        ctx.write(Unpooled.copiedBuffer(fix.serialize()));
        connections.put(id, ctx);
        ctx.flush();

        System.out.printf("New %s with id: %d", isBroker ? "Broker" : "Market", id);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
