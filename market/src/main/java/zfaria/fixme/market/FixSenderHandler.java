package zfaria.fixme.market;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import zfaria.fixme.core.notation.Fix;
import zfaria.fixme.core.notation.FixSerializer;
import zfaria.fixme.core.notation.FixTag;

public class FixSenderHandler extends SimpleChannelInboundHandler {

    private int id;

    private ChannelHandlerContext context;

    private MarketWindow window;

    public FixSenderHandler() {
        window = new MarketWindow();
        window.addToQueue("Connecting...");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        window.addToQueue("Connected.");
        System.out.println("Connected");
        context = ctx;
        //Fix f = new Fix(FixTag.MSG_NEW_ORDER);
        //f.addTag(new FixTag(FixTag.DESTINATION_ID, "100000"));
        //byte[] buff = f.serialize();

        //ctx.writeAndFlush(Unpooled.wrappedBuffer(buff));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        Fix f = FixSerializer.deserialize(o);

        if (f.getMessageType().equals(FixTag.MSG_CONNECT)) {
            handleNewConnection(ctx, f);
        }
        System.out.println(f.toString());
    }

    public int getId() {
        return id;
    }

    private void handleNewConnection(ChannelHandlerContext ctx, Fix f) {
        String idString = f.getTag(FixTag.CONNECT_ID).getValue();
        id = Integer.parseInt(idString);
        System.out.printf("My id is: %d\n", id);
    }
}
