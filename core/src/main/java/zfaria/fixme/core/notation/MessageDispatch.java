package zfaria.fixme.core.notation;

import io.netty.channel.ChannelHandlerContext;

public interface MessageDispatch {

    void notify(ChannelHandlerContext ctx, Fix f);

}
