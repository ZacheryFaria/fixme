package zfaria.fixme.broker;

import zfaria.fixme.core.fix.Fix;
import zfaria.fixme.core.transaction.Listing;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Holdings extends AbstractTableModel {

    private String[] columnNames = {"Symbol", "Quantity"};

    private List<Listing> data = new ArrayList<>();

    protected double funds = 10000;

    public void addHolding(Listing l) {
        for (Listing i : data) {
            if (i.getName().equals(l.getName())) {
                i.addQty(l.getQty());
                return;
            }
        }
        data.add(l);
    }

    /**
     * Returns true if the holding was successfully removed.
     * Returns false if it does not exist or does not have enough.
     */
    public boolean removeHolding(Listing l) {

        Iterator<Listing> itr = data.iterator();

        while (itr.hasNext()) {
            Listing i = itr.next();
            if (i.getName().equals(l.getName())) {
                if (i.getQty() < l.getQty()) {
                    return false;
                }
                i.removeQty(l.getQty());
                if (i.isEmpty()) {
                    itr.remove();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int i, int field) {
        Listing instrument = data.get(i);

        switch (field) {
            case 0:
                return instrument.getName();
            case 1:
                return instrument.getQty();
            case 2:
                return instrument.getPrice();
            case 3:
                return instrument.getValue();
        }
        return null;
    }

    public void transactionComplete(Fix f) {
        Listing l = new Listing(f);

        if (f.getTag(Fix.SIDE).equals(Fix.SIDE_BUY)) {
            funds -= l.getValue();
            addHolding(l);
            fireTableDataChanged();
        } else {
            funds += l.getValue();
        }
    }
}
