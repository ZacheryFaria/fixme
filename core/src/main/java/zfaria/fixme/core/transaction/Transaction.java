package zfaria.fixme.core.transaction;

public class Transaction {

    private String symbol;

    private int quantity;

    private double price;

    private int buyer;

    private int seller;

    public Transaction(String symbol, int quantity, double price, int buyer, int seller) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.buyer = buyer;
        this.seller = seller;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getValue() {
        return price * quantity;
    }

    public int getBuyer() {
        return buyer;
    }

    public int getSeller() {
        return seller;
    }
}
