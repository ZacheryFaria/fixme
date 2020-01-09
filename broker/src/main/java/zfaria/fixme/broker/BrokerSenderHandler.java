package zfaria.fixme.broker;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import zfaria.fixme.broker.Broker;
import zfaria.fixme.broker.BrokerWindow;
import zfaria.fixme.core.notation.Fix;
import zfaria.fixme.core.notation.FixSenderHandler;
import zfaria.fixme.core.notation.FixSerializer;
import zfaria.fixme.core.notation.MessageDispatch;

import java.util.HashMap;
import java.util.Map;

public class BrokerSenderHandler extends FixSenderHandler {

    private int id;

    public BrokerSenderHandler() {
        super(new BrokerWindow());
    }

}
