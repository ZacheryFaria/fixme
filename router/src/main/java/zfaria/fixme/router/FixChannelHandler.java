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
        int val = 0;

        try {
            val = Integer.parseInt(deststr);
        } catch (NumberFormatException e) {
            return null;
        }

        return connections.getOrDefault(val, null);
    }

    private void fireMarketMessage(ChannelHandlerContext ctx, Fix f) {
        ChannelHandlerContext out = getDestinationContext(f);

        if (out == null) {
            out = ctx;
            f = getNoSuchDestinationResponse(f);
        }

        out.writeAndFlush(Unpooled.copiedBuffer(f.serialize())).syncUninterruptibly();
    }

    private void fireBrokerMessage(ChannelHandlerContext ctx, Fix f) {
        for (Map.Entry<Integer, ChannelHandlerContext> e : connections.entrySet()) {
            if (e.getKey() < 200000 && e.getValue().channel().isActive()) {
                e.getValue().writeAndFlush(Unpooled.copiedBuffer(f.serialize()));
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Fix f = FixSerializer.deserialize(msg);
        System.out.println(f.toString());
        if (!isBroker) {
            fireMarketMessage(ctx, f);
        } else {
            fireBrokerMessage(ctx, f);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        int id = isBroker ? brokerID++ : marketID++;
        Fix fix = new Fix(Fix.MSG_CONNECT);
        fix.addTag(Fix.CONNECT_ID, Integer.toString(id));
        ctx.writeAndFlush(Unpooled.copiedBuffer(fix.serialize())).syncUninterruptibly();
        connections.put(id, ctx);
        System.out.printf("New %s with id: %d\n", isBroker ? "Broker" : "Market", id);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public Fix getNoSuchDestinationResponse(Fix f) {
        Fix fix = new Fix(Fix.MSG_NO_DESTINATION);
        fix.addTag(Fix.SENDER_ID, f.getTag(Fix.SENDER_ID));
        fix.addTag(Fix.DESTINATION_ID, f.getTag(Fix.DESTINATION_ID));
        return fix;
    }

}
