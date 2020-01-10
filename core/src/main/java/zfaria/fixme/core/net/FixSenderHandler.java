package zfaria.fixme.core.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import zfaria.fixme.core.fix.Fix;
import zfaria.fixme.core.swing.FixWindow;

import java.util.HashMap;
import java.util.Map;

public class FixSenderHandler extends SimpleChannelInboundHandler {

    protected int id;

    protected FixWindow window;

    protected ChannelHandlerContext ctx;

    protected Map<String, MessageDispatch> dispatch;

    public FixSenderHandler(FixWindow window) {
        this.window = window;
        //this.window.addMessage("Connecting...");

        dispatch = new HashMap<>();
        dispatch.put(Fix.MSG_CONNECT, ((ctx, f) -> {
            String idString = f.getTag(Fix.CONNECT_ID);
            id = Integer.parseInt(idString);
            window.addMessage("ID: " + id);
        }));

        dispatch.put(Fix.MSG_NEW_ORDER, (ctx, f) -> {
            window.fireOrderEvent(f);
        });

        dispatch.put(Fix.MSG_NO_DESTINATION, ((ctx1, f) -> {
            window.addMessage("Unable to find destination id " + f.getTag(Fix.DESTINATION_ID));
        }));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        window.addMessage("Connected.");
        this.ctx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) {
        ByteBuf buf = (ByteBuf)o;

        while (buf.isReadable()) {
            byte len = buf.readByte();
            byte[] buff = new byte[len];
            buf.readBytes(buff);

            Fix f = new Fix(buff);

            dispatch.get(f.getTag(Fix.MSG_TYPE)).notify(ctx, f);
        }
    }

    public void sendMessage(Fix f) {
        byte[] buff = f.serialize();

        ByteBuf buf = Unpooled.buffer(buff.length);

        buf.writeByte(buff.length);
        buf.writeBytes(buff);

        ctx.writeAndFlush(buf);
    }

    public int getId() {
        return id;
    }

}
