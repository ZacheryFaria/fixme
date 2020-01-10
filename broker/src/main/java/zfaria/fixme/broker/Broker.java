package zfaria.fixme.broker;

import zfaria.fixme.core.net.TradeBootstrap;

public class Broker {

    public static void main(String args[]) {
        TradeBootstrap bootstrap = new TradeBootstrap(5000, new BrokerWindow());
        bootstrap.run();
    }

}
