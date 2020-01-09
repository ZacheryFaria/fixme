package zfaria.fixme.core.instruments;

import zfaria.fixme.core.notation.Fix;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Listing {

    private String name;
    private int qty;
    private double price;
    private int ownerId;
    private int id;

    public Listing(String name, int qty, double price, int owner) {
        this(name, qty, price, owner, -1);
    }

    public Listing(String name, int qty, double price, int owner, int id) {
        this.name = name;
        this.qty = qty;
        this.price = price;
        this.ownerId = owner;
        this.id = id;
    }

    public Listing(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.ownerId = set.getInt("owner");
        this.qty = set.getInt("quantity");
        this.price = set.getDouble("price");
        this.name = set.getString("symbol");
    }

    public Listing(Fix f) {
        this.name = f.getTag(Fix.SYMBOL);
        this.qty = Integer.parseInt(f.getTag(Fix.ORDERQTY));
        this.price = Double.parseDouble(f.getTag(Fix.PRICE));
        this.ownerId = Integer.parseInt(f.getTag(Fix.SENDER_ID));
    }

    public String getName() {
        return name;
    }

    public int getQty() {
        return qty;
    }

    public double getPrice() {
        return price;
    }

    public double getValue() {
        return price * qty;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void addQty(int qty) {
        this.qty += qty;
    }

    public void removeQty(int qty) {
        this.qty -= qty;
    }

    public boolean isEmpty() {
        return qty <= 0;
    }

    /**
     * Handles an order for @qty shares.
     * It will either fill the order, or partially fill it, depending on how many shares
     * being bought.
     *
     * @param qty
     * @return A Listing used for the response and to be placed in the transaction database,
     * not an actual listing.
     */
    public Listing handleOrder(int qty) {
        int filledQty = 0;

        if (qty <= this.qty) {
            this.qty -= qty;
            filledQty = qty;
        } else {
            filledQty = this.qty;
            this.qty = 0;
        }

        Listing l = new Listing(name, filledQty, price, ownerId);
        return l;
    }
}
