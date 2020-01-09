package zfaria.fixme.market;

import zfaria.fixme.core.notation.FixSenderHandler;

public class MarketSenderHandler extends FixSenderHandler {

    public MarketSenderHandler() {
        super(new MarketWindow());
    }

}
