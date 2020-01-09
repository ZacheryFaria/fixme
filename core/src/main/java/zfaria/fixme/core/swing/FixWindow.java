package zfaria.fixme.core.swing;

import zfaria.fixme.core.notation.Fix;
import zfaria.fixme.core.notation.FixSenderHandler;

public interface FixWindow {

    void addMessage(String msg);

    void addSender(FixSenderHandler handler);

    /**
     * Called when the dispatcher receives a Fix.MSG_NEW_ORDER
     * Has differing implementations on broker/market.
     * @param message
     */
    void fireOrderEvent(Fix message);

}
