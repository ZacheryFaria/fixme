package zfaria.fixme.market;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class MarketWindow {

    private JFrame window;
    private JTextArea log;
    private JScrollPane logPane;
    private JTextField symbol;
    private JTextField quantity;
    private JTextField price;
    private JButton submit;

    public MarketWindow() {
        window = new JFrame("Market");
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new GridBagLayout());
        window.setSize(1280, 720);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridwidth = 3;
        c.weightx = .1;
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

        c.gridy = 1;
        c.ipady = 10;
        c.ipadx = 200;
        c.gridwidth = 1;
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
        submit = new JButton("Submit");
        window.add(submit, c);


        window.pack();
    }

    public void addToQueue(String msg) {
        log.append(msg + "\n");
    }

}
