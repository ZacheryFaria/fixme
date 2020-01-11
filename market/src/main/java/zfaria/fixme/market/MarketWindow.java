package zfaria.fixme.market;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import zfaria.fixme.core.database.Database;
import zfaria.fixme.core.transaction.Listing;
import zfaria.fixme.core.net.TradeBootstrap;
import zfaria.fixme.core.fix.Fix;
import zfaria.fixme.core.swing.FixWindow;
import zfaria.fixme.core.swing.VanishingTextField;
import zfaria.fixme.core.transaction.Transaction;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static zfaria.fixme.core.net.TradeBootstrap.handler;

public class MarketWindow extends FixWindow {

    private JTextArea log;
    private JScrollPane logPane;
    private JTextField symbol;
    private JTextField quantity;
    private JTextField price;
    private JButton add;

    private JTable tradeTable;

    private TradeList tradeList = new TradeList();

    public MarketWindow() {
        super("Market");

        handler = TradeBootstrap.handler;

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
        tradeTable = new JTable(tradeList);
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
            String symb = symbol.getText().toUpperCase();
            Integer qty = Ints.tryParse(quantity.getText());
            Double pric = Doubles.tryParse(price.getText());
            if (qty == null || pric == null) {
                addMessage("Quantity or pric are not numbers.");
                return;
            }
            Database.addNewListing(new Listing(symb, qty, pric, 0));
            tradeList.updateList();
        });
        window.add(add, c);

        window.pack();
    }

    public void addMessage(String msg) {
        log.append(msg + "\n");
    }

    public void fireOrderEvent(Fix message) {
        message.addTag(Fix.SYMBOL, message.getTag(Fix.SYMBOL).toUpperCase());
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

            int shares = getBuyableShares(funds, want, l);

            Transaction transaction = l.fillOrder(shares, buyer);

            l.removeQty(transaction.getQuantity());
            Database.modifyListing(l);

            Database.addTransaction(transaction, buyer);

            want.removeQty(transaction.getQuantity());
            funds -= transaction.getValue();

            notifyParties(transaction, buyer, l.getOwnerId());

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

    private void notifyParties(Transaction transaction, int buyer, int seller) {
        Fix f = new Fix(Fix.MSG_NEW_ORDER);
        f.addTag(Fix.ORDSTATUS, Fix.ORDSTATUS_PARTIAL);
        f.addTag(Fix.ORDERQTY, transaction.getQuantity());
        f.addTag(Fix.SYMBOL, transaction.getSymbol());
        f.addTag(Fix.PRICE, transaction.getPrice());
        f.addTag(Fix.SENDER_ID, 0);

        f.addTag(Fix.SIDE, Fix.SIDE_BUY);
        f.addTag(Fix.DESTINATION_ID, buyer);
        handler.sendMessage(f);

        f.addTag(Fix.SIDE, Fix.SIDE_SELL);
        f.addTag(Fix.DESTINATION_ID, seller);
        handler.sendMessage(f);
    }

    private void rejectOrder(Fix f) {
        Fix ff = new Fix(Fix.MSG_NEW_ORDER);
        ff.addTag(Fix.ORDSTATUS, Fix.ORDSTATUS_REJECTED);
        ff.addTag(Fix.DESTINATION_ID, f.getTag(Fix.SENDER_ID));
        ff.addTag(Fix.SIDE, f.getTag(Fix.SIDE));

        handler.sendMessage(ff);
    }

    private int getBuyableShares(double funds, Listing want, Listing l) {
        Double maxDouble = Math.floor(funds / l.getPrice());
        int max = maxDouble.intValue(); // maximum amount of stock can purchase
        max = Ints.min(max, want.getQty(), l.getQty());

        return max;
    }
}
