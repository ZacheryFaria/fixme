package zfaria.fixme.market;

import zfaria.fixme.core.database.Database;
import zfaria.fixme.core.instruments.Listing;
import zfaria.fixme.core.notation.Fix;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TradeList extends AbstractTableModel {

    private String[] columnNames = {"Symbol", "Qty", "Price", "Owner"};

    public TradeList() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return Database.getListingCount();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int i, int i1) {
        List<Listing> list = Database.getListings();
        if (i >= list.size()) {
            return null;
        }
        Listing l = list.get(i);
        switch (i1) {
            case 0:
                return l.getName();
            case 1:
                return l.getQty();
            case 2:
                return l.getPrice();
            case 3:
                return l.getOwnerId();
        }
        return null;
    }
}
