package zfaria.fixme.core.database;

import zfaria.fixme.core.instruments.Listing;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static Connection conn;

    static {
        try {
            String url = "jdbc:sqlite:test.db";
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getListingCount() {
        String sql = "SELECT COUNT(id) FROM listings";
        try (PreparedStatement state = conn.prepareStatement(sql)) {
            ResultSet set = state.executeQuery();
            return set.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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

    public static void removeListingsOfOwner(int id) {
        String sql = "DELETE FROM listings where owner = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Listing getListingById(int id) {
        String sql = "SELECT * FROM listings WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet set = stmt.executeQuery();
            Listing listing = new Listing(set);
            set.close();
            return listing;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Listing> getListingByOwner(int id) {
        String sql = "SELECT * FROM listings WHERE owner = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return generateListFromResultSet(stmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Listing> getListings() {
        String sql = "SELECT * FROM listings";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            return generateListFromResultSet(stmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

        // If the quantity is zero we are goign to just remove it.
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

    public static void addTransaction(Listing l, int buyer) {

    }


}
