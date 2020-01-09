package zfaria.fixme.broker;

import zfaria.fixme.core.instruments.Listing;
import zfaria.fixme.core.notation.Fix;
import zfaria.fixme.core.notation.FixSenderHandler;
import zfaria.fixme.core.swing.FixWindow;
import zfaria.fixme.core.swing.VanishingTextField;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class BrokerWindow implements FixWindow {

    private JFrame window;
    private JTextArea log;
    private JScrollPane logPane;
    private JTextField symbol;
    private JTextField quantity;
    private JTextField price;
    private JButton buy;
    private JButton sell;
    private JLabel funds;

    private JTable holdingsTable;
    private Holdings holdings = new Holdings();

    private FixSenderHandler sender;

    public BrokerWindow() {
        window = new JFrame("Broker");
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(640, 480);
        window.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        c.gridwidth = 3;
        c.weightx = .1;
        c.weighty = .1;
        c.ipady = 300;
        c.ipadx = 300;
        log = new JTextArea();
        log.setEditable(false);
        log.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        logPane = new JScrollPane(log);
        logPane.setAutoscrolls(true);
        window.add(logPane, c);

        c.gridx = 3;
        c.gridwidth = 1;
        c.ipadx = 100;
        holdingsTable = new JTable(holdings);
        holdingsTable.setFillsViewportHeight(true);
        window.add(new JScrollPane(holdingsTable), c);

        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 1;
        c.ipady = 10;
        symbol = new VanishingTextField("Sym");
        window.add(symbol, c);

        c.gridx = 1;
        quantity = new VanishingTextField("Qty");
        window.add(quantity, c);

        c.gridx = 2;
        price = new VanishingTextField("Price");
        window.add(price, c);

        c.gridx = 0;
        c.gridy = 2;
        buy = new JButton("Buy");
        buy.addActionListener((actionEvent -> {
            placeOrder(Fix.SIDE_BUY);
        }));
        window.add(buy, c);

        c.gridx = 1;
        sell = new JButton("Sell");
        sell.addActionListener((actionEvent -> {
            placeOrder(Fix.SIDE_SELL);
        }));
        window.add(sell, c);

        c.gridx = 2;
        funds = new JLabel("Funds");
        updateFunds();
        window.add(funds, c);

        //holdings.addHolding(new Listing("TSLA", 100, new BigDecimal("123"), 0));

        window.pack();
    }

    public void addMessage(String msg) {
        log.append(msg + "\n");
    }

    public void addSender(FixSenderHandler handler) {
        this.sender = handler;
    }

    /**
     * Creates a fix message and dispatches it
     * @param orderType is either Buy or Sell
     */
    private void placeOrder(String orderType) {
        Fix f = new Fix(Fix.MSG_NEW_ORDER);
        f.addTag(Fix.SIDE, orderType);
        f.addTag(Fix.SENDER_ID, Integer.toString(sender.getId()));
        f.addTag(Fix.SYMBOL, symbol.getText());
        f.addTag(Fix.PRICE, price.getText());
        f.addTag(Fix.ORDERQTY, quantity.getText());
        f.addTag(Fix.FUNDS, holdings.funds);

        boolean sendMessage = true;
        if (orderType.equals(Fix.SIDE_SELL)) {
            sendMessage = holdings.removeHolding(new Listing(f));
            holdingsTable.updateUI();
        }

        if (sendMessage) {
            sender.sendMessage(f);
        } else {
            addMessage("Not enough inventory.");
        }
    }

    public void updateFunds() {
        funds.setText(String.format("Funds $%f", holdings.funds));
    }

    /**
     * Is called when the transaction was successful. Used to update the ui.
     * @param message
     */
    public void fireOrderEvent(Fix message) {
        String status = message.getTag(Fix.ORDSTATUS);

        String side = message.getTag(Fix.SIDE).equals(Fix.SIDE_SELL) ? "Sell" : "Buy";

        addMessage(message.toString());

        switch (status) {
            case Fix.ORDSTATUS_REJECTED:
                addMessage("Order rejected, instrument not sold or not enough funds.");
                break;
            case Fix.ORDSTATUS_ACKNOWLEDGE:
                addMessage("Sale order received");
                break;
            case Fix.ORDSTATUS_PARTIAL:
                addMessage(String.format(side + " completed. %s of %s for %s each.",
                        message.getTag(Fix.ORDERQTY),
                        message.getTag(Fix.SYMBOL),
                        message.getTag(Fix.PRICE)));
                holdings.transactionComplete(message);
                updateFunds();
                //holdingsTable.updateUI();
                break;
        }
    }
}
