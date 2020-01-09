package zfaria.fixme.broker;

import zfaria.fixme.core.instruments.Listing;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class Holdings extends AbstractTableModel {

    private String[] columnNames = {"Symbol", "Quantity"};

    private List<Listing> data = new ArrayList<Listing>();

    public void addHolding(Listing i) {
        data.add(i);
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

}
