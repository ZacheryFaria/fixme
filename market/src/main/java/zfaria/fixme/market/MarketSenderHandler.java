package zfaria.fixme.market;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import zfaria.fixme.core.database.Database;
import zfaria.fixme.core.notation.Fix;
import zfaria.fixme.core.notation.FixSenderHandler;
import zfaria.fixme.core.notation.FixSerializer;
import zfaria.fixme.core.notation.MessageDispatch;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

public class MarketSenderHandler extends FixSenderHandler {

    public MarketSenderHandler() {
        super(new MarketWindow());
    }

}
