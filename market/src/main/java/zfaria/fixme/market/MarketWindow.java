package zfaria.fixme.market;

import zfaria.fixme.core.database.Database;
import zfaria.fixme.core.instruments.Listing;
import zfaria.fixme.core.notation.Fix;
import zfaria.fixme.core.notation.FixSenderHandler;
import zfaria.fixme.core.swing.FixWindow;
import zfaria.fixme.core.swing.VanishingTextField;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MarketWindow implements FixWindow {

    private JFrame window;
    private JTextArea log;
    private JScrollPane logPane;
    private JTextField symbol;
    private JTextField quantity;
    private JTextField price;
    private JButton add;

    private JTable tradeTable;

    private FixSenderHandler sender;

    private TradeList list = new TradeList();

    public MarketWindow() {
        window = new JFrame("Market");
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new GridBagLayout());
        window.setSize(1280, 720);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        c.gridwidth = 3;
        c.weightx = .1;
        c.weighty = .1;
        c.ipady = 400;
        c.ipadx = 600;
        log = new JTextArea();
        log.setEditable(false);
        log.setSize(500, 500);
        log.setLocation(0, 0);
        log.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        logPane = new JScrollPane(log);
        logPane.setAutoscrolls(true);
        window.add(logPane, c);

        c.gridx = 3;
        c.gridwidth = 1;
        c.ipadx = 200;
        tradeTable = new JTable(list);
        tradeTable.setFillsViewportHeight(true);
        window.add(new JScrollPane(tradeTable), c);

        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 1;
        c.ipady = 10;
        c.ipadx = 200;
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
        add = new JButton("Add");
        window.add(add, c);

        window.pack();
    }

    public void addMessage(String msg) {
        log.append(msg + "\n");
    }

    public void addSender(FixSenderHandler handler) {
        this.sender = handler;
    }

    public void newOrderEvent(Fix f) {
        log.append(f + "\n");
        tradeTable.updateUI();
        if (f.getTag(Fix.SIDE).equals(Fix.SIDE_SELL)) {
            Database.addNewListing(new Listing(f));
        } else {
            processTransaction(f);
        }
    }

    private void processTransaction(Fix f) {
        Listing buy = new Listing(f);
        String sym = f.getTag(Fix.SYMBOL);

        List<Listing> list = Database.getListingsBySymbol(sym);

        if (list.size() == 0) {
            rejectedOrder(f);
            return;
        }

        for (Listing l : list) {

        }
    }

    private void rejectedOrder(Fix f) {
        Fix ff = new Fix(Fix.MSG_NEW_ORDER);
        ff.addTag(Fix.ORDSTATUS, Fix.ORDSTATUS_REJECTED);
        ff.addTag(Fix.DESTINATION_ID, f.getTag(Fix.SENDER_ID));

        sender.sendMessage(ff);
    }

}
