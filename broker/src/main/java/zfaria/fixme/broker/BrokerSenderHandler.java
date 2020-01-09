package zfaria.fixme.broker;

import zfaria.fixme.core.notation.FixSenderHandler;

public class BrokerSenderHandler extends FixSenderHandler {

    public BrokerSenderHandler() {
        super(new BrokerWindow());
    }

}
