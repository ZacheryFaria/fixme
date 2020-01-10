package zfaria.fixme.market;

import zfaria.fixme.core.database.Database;
import zfaria.fixme.core.instruments.Listing;
import zfaria.fixme.core.net.TradeBootstrap;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.List;

public class Market {

    public static void main(String args[]) {
        TradeBootstrap bootstrap = new TradeBootstrap(5001, new MarketWindow());
        bootstrap.run();
    }

}
