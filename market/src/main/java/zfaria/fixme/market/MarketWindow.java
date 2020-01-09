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
        window.setSize(640, 480);

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
        c.ipadx = 150;
        tradeTable = new JTable(list);
        tradeTable.setFillsViewportHeight(true);
        window.add(new JScrollPane(tradeTable), c);

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
        add = new JButton("Add");
        add.addActionListener(actionEvent -> {
            String symb = symbol.getText();
            int qty = Integer.parseInt(quantity.getText());
            double pric = Double.parseDouble(price.getText());
            Database.addNewListing(new Listing(symb, qty, pric, 0));
            tradeTable.updateUI();
        });
        window.add(add, c);

        window.pack();
    }

    public void addMessage(String msg) {
        log.append(msg + "\n");
    }

    public void addSender(FixSenderHandler handler) {
        this.sender = handler;
    }

    public void fireOrderEvent(Fix message) {
        log.append(message + "\n");
        tradeTable.updateUI();
        if (message.getTag(Fix.SIDE).equals(Fix.SIDE_SELL)) {
            Database.addNewListing(new Listing(message));
        } else {
            processTransaction(message);
        }
    }

    private void processTransaction(Fix f) {
        Listing want = new Listing(f);
        String sym = f.getTag(Fix.SYMBOL);

        List<Listing> list = Database.getListingsBySymbol(sym);

        if (list.size() == 0) {
            rejectOrder(f);
            return;
        }

        double funds = Double.parseDouble(f.getTag(Fix.FUNDS));

        int buyer = Integer.parseInt(f.getTag(Fix.SENDER_ID));

        boolean boughtStock = false;

        for (Listing l : list) {
            // If the listing costs more, we can't purchase anything else.
            if (want.getPrice() < l.getPrice()) {
                break;
            }

            // Skip when it's our own posting.
            if (want.getOwnerId() == l.getOwnerId()) {
                continue;
            }

            Double maxDouble = Math.floor(funds / l.getPrice());
            int max = maxDouble.intValue(); // maximum amount of stock can purchase
            max = Integer.min(max, want.getQty());
            max = Integer.min(max, l.getQty());
            if (max == 0) {
                break;
            }

            Listing transaction = l.handleOrder(max);

            funds -= transaction.getValue();
            want.removeQty(transaction.getQty());

            Database.addTransaction(transaction, buyer);
            Database.modifyListing(l);

            notifyBuyer(transaction, buyer);

            notifyOwner(transaction);

            boughtStock = true;

            // order is filled.
            if (want.getQty() == 0) {
                break;
            }
        }

        // This means we failed to purchase any stock, therefore it is a failed order.
        if (!boughtStock) {
            rejectOrder(f);
        }
    }

    private void notifyBuyer(Listing l, int destination) {
        Fix f = new Fix(Fix.MSG_NEW_ORDER);
        f.addTag(Fix.ORDSTATUS, Fix.ORDSTATUS_PARTIAL);
        f.addTag(Fix.SIDE, Fix.SIDE_BUY);
        f.addTag(Fix.ORDERQTY, l.getQty());
        f.addTag(Fix.SYMBOL, l.getName());
        f.addTag(Fix.PRICE, l.getPrice());
        f.addTag(Fix.DESTINATION_ID, destination);
        f.addTag(Fix.SENDER_ID, 0);

        System.out.println(f);

        sender.sendMessage(f);
    }

    private void notifyOwner(Listing l) {
        Fix f = new Fix(Fix.MSG_NEW_ORDER);
        f.addTag(Fix.ORDSTATUS, Fix.ORDSTATUS_PARTIAL);
        f.addTag(Fix.SIDE, Fix.SIDE_SELL);
        f.addTag(Fix.ORDERQTY, l.getQty());
        f.addTag(Fix.SYMBOL, l.getName());
        f.addTag(Fix.PRICE, l.getPrice());
        f.addTag(Fix.DESTINATION_ID, l.getOwnerId());
        f.addTag(Fix.SENDER_ID, 0);

        System.out.println(f);

        sender.sendMessage(f);
    }

    private void rejectOrder(Fix f) {
        Fix ff = new Fix(Fix.MSG_NEW_ORDER);
        ff.addTag(Fix.ORDSTATUS, Fix.ORDSTATUS_REJECTED);
        ff.addTag(Fix.DESTINATION_ID, f.getTag(Fix.SENDER_ID));
        ff.addTag(Fix.SIDE, f.getTag(Fix.SIDE));

        sender.sendMessage(ff);
    }

}
