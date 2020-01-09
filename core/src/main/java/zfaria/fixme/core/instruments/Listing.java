package zfaria.fixme.core.instruments;

import zfaria.fixme.core.notation.Fix;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Listing {

    private String name;
    private int qty;
    private BigDecimal price;
    private int ownerId;
    private int id;

    public Listing(String name, int qty, BigDecimal price, int owner) {
        this(name, qty, price, owner, -1);
    }

    public Listing(String name, int qty, BigDecimal price, int owner, int id) {
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
        this.price = set.getBigDecimal("price");
        this.name = set.getString("symbol");
    }

    public Listing(Fix f) {
        this.name = f.getTag(Fix.SYMBOL);
        this.qty = Integer.parseInt(f.getTag(Fix.ORDERQTY));
        this.price = new BigDecimal(f.getTag(Fix.PRICE));
        this.ownerId = Integer.parseInt(f.getTag(Fix.SENDER_ID));
    }

    public String getName() {
        return name;
    }

    public int getQty() {
        return qty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getValue() {
        return price.multiply(new BigDecimal(qty));
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
}
