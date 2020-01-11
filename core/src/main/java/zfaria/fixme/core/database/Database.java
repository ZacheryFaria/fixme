package zfaria.fixme.core.database;

import zfaria.fixme.core.transaction.Listing;
import zfaria.fixme.core.transaction.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static Connection conn;

    static {
        try {
            String url = "jdbc:sqlite:fixme.db";
            conn = DriverManager.getConnection(url);
            init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void init() {
        try (Statement state = conn.createStatement()) {
            state.executeUpdate("CREATE TABLE IF NOT EXISTS listings " +
                    "(id    INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "owner  INTEGER              NOT NULL," +
                    "symbol CHAR(8)          NOT NULL," +
                    "price  DECIMAL(10, 5)  NOT NULL," +
                    "quantity INTEGER NOT NULL)");

            state.executeUpdate("CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "buyer INTEGER," +
                    "seller INTEGER," +
                    "symbol CHAR(8)," +
                    "price DECIMAL(10, 5)," +
                    "quantity INTEGER" +
                    ");");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int addNewListing(Listing l) {
        String sql = "INSERT into listings (owner, symbol, price, quantity) values (?, ?, ?, ?)";
        try (PreparedStatement state = conn.prepareStatement(sql)) {
            state.setInt(1, l.getOwnerId());
            state.setString(2, l.getName());
            state.setDouble(3, l.getPrice());
            state.setInt(4, l.getQty());
            state.executeUpdate();
            ResultSet set = state.getGeneratedKeys();
            l.setId(set.getInt(1));
            return set.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<Listing> getListings() {
        String sql = "SELECT * FROM listings";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            return generateListFromResultSet(stmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static List<Listing> generateListFromResultSet(ResultSet set) throws SQLException {
        List<Listing> list = new ArrayList<>();
        while (set.next()) {
            list.add(new Listing(set));
        }
        set.close();
        return list;
    }

    public static List<Listing> getListingsBySymbol(String symbol) {
        String sql = "select * from listings where symbol = ? order by price";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, symbol);
            return generateListFromResultSet(stmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void modifyListing(Listing l) {
        String sql = "update listings set quantity = ? where id = ?";

        // If the quantity is zero we are going to just remove it.
        if (l.getQty() <= 0) {
            removeListing(l);
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, l.getQty());
            stmt.setInt(2, l.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeListing(Listing l) {
        String sql = "delete from listings where id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, l.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addTransaction(Transaction t, int buyer) {
        String sql = "INSERT into transactions (seller, buyer, symbol, price, quantity) values (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, t.getSeller());
            stmt.setInt(2, t.getBuyer());
            stmt.setString(3, t.getSymbol());
            stmt.setDouble(4, t.getPrice());
            stmt.setInt(5, t.getQuantity());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
