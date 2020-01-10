package zfaria.fixme.core.transaction;

public class Transaction {

    private String symbol;

    private int quantity;

    private double price;

    public Transaction(String symbol, int quantity, double price) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
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
}
