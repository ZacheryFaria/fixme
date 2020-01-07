package zfaria.fixme.market;

import io.netty.channel.*;
import zfaria.fixme.core.notation.Fix;
import zfaria.fixme.core.notation.FixSerializer;

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

        if (f.getTag(Fix.MSG_TYPE).equals(Fix.MSG_CONNECT)) {
            handleNewConnection(ctx, f);
        }
        System.out.println(f.toString());
    }

    public int getId() {
        return id;
    }

    private void handleNewConnection(ChannelHandlerContext ctx, Fix f) {
        String idString = f.getTag(Fix.CONNECT_ID);
        id = Integer.parseInt(idString);
        System.out.printf("My id is: %d\n", id);
    }
}
