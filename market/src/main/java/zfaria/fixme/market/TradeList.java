package zfaria.fixme.market;

import zfaria.fixme.core.database.Database;
import zfaria.fixme.core.transaction.Listing;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TradeList extends AbstractTableModel {

    private String[] columnNames = {"Symbol", "Qty", "Price", "Owner"};

    private List<Listing> list = new ArrayList<>();

    public TradeList() {
        Thread t = new Thread(() -> {
            while (true) {
                updateList();
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void updateList() {
        list = Database.getListings();
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int i, int i1) {
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
