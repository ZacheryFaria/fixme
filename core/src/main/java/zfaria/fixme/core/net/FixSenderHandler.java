package zfaria.fixme.core.net;

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
        Fix f = (Fix)o;

        dispatch.get(f.getTag(Fix.MSG_TYPE)).notify(ctx, f);
    }

    public void sendMessage(Fix f) {
        ctx.writeAndFlush(f);
    }

    public int getId() {
        return id;
    }

}
