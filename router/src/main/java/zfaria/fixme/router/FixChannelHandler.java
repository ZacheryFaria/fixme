package zfaria.fixme.router;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import zfaria.fixme.core.fix.Fix;

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

        sendMessage(out, f);
    }

    private void fireBrokerMessage(ChannelHandlerContext ctx, Fix f) {
        for (Map.Entry<Integer, ChannelHandlerContext> e : connections.entrySet()) {
            if (e.getKey() < 200000 && e.getValue().channel().isActive()) {
                sendMessage(e.getValue(), f);
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf)msg;

        while (buf.isReadable()) {
            byte len = buf.readByte();
            byte[] buff = new byte[len];
            buf.readBytes(buff);

            Fix f = new Fix(buff);

            if (!isBroker) {
                fireMarketMessage(ctx, f);
            } else {
                fireBrokerMessage(ctx, f);
            }
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        int id = isBroker ? brokerID++ : marketID++;
        Fix fix = new Fix(Fix.MSG_CONNECT);
        fix.addTag(Fix.CONNECT_ID, Integer.toString(id));
        sendMessage(ctx, fix);
        connections.put(id, ctx);
        System.out.printf("New %s with id: %d\n", isBroker ? "Broker" : "Market", id);
    }

    private void sendMessage(ChannelHandlerContext ctx, Fix f) {
        byte[] buff = f.serialize();
        ByteBuf buf = Unpooled.buffer(buff.length + 1);
        buf.writeByte(buff.length);
        buf.writeBytes(buff);
        ctx.writeAndFlush(buf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    public Fix getNoSuchDestinationResponse(Fix f) {
        Fix fix = new Fix(Fix.MSG_NO_DESTINATION);
        fix.addTag(Fix.SENDER_ID, f.getTag(Fix.SENDER_ID));
        fix.addTag(Fix.DESTINATION_ID, f.getTag(Fix.DESTINATION_ID));
        return fix;
    }

}
