package zfaria.fixme.core.notation;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import zfaria.fixme.core.swing.FixWindow;

import java.util.HashMap;
import java.util.Map;

public abstract class FixSenderHandler extends SimpleChannelInboundHandler {

    protected int id;

    protected FixWindow window;

    protected ChannelHandlerContext ctx;

    protected Map<String, MessageDispatch> dispatch;

    public FixSenderHandler(FixWindow window) {
        this.window = window;
        this.window.addMessage("Connecting...");
        this.window.addSender(this);

        dispatch = new HashMap<>();
        dispatch.put(Fix.MSG_CONNECT, ((ctx, f) -> {
            String idString = f.getTag(Fix.CONNECT_ID);
            id = Integer.parseInt(idString);
            window.addMessage("ID: " + id);
        }));

        dispatch.put(Fix.MSG_NEW_ORDER, (ctx, f) -> {
            window.newOrderEvent(f);
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        window.addMessage("Connected.");
        this.ctx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        Fix f = FixSerializer.deserialize(o);

        dispatch.get(f.getTag(Fix.MSG_TYPE)).notify(ctx, f);
    }

    public void sendMessage(Fix f) {
        byte[] buff = f.serialize();

        ctx.writeAndFlush(Unpooled.wrappedBuffer(buff));
    }

    public int getId() {
        return id;
    }

}
