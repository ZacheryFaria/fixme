package zfaria.fixme.core.net;

import io.netty.channel.ChannelHandlerContext;
import zfaria.fixme.core.fix.Fix;

public interface MessageDispatch {

    void notify(ChannelHandlerContext ctx, Fix f);

}
