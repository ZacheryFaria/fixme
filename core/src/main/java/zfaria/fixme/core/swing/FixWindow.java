package zfaria.fixme.core.swing;

import zfaria.fixme.core.net.TradeBootstrap;
import zfaria.fixme.core.notation.Fix;
import zfaria.fixme.core.net.FixSenderHandler;

import javax.swing.*;
import java.awt.*;

public abstract class FixWindow {

    protected JFrame window;

    public FixWindow(String title) {
        window = new JFrame(title);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new GridBagLayout());
    }

    public void updateInternalHandler() {

    }

    public abstract void addMessage(String msg);

    /**
     * Called when the dispatcher receives a Fix.MSG_NEW_ORDER
     * Has differing implementations on broker/market.
     * @param message
     */
    public abstract void fireOrderEvent(Fix message);

}
