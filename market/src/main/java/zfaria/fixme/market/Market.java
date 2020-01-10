package zfaria.fixme.market;

import zfaria.fixme.core.net.TradeBootstrap;

public class Market {

    public static void main(String args[]) {
        TradeBootstrap bootstrap = new TradeBootstrap(5001, new MarketWindow());
        bootstrap.run();
    }

}
