package it.unibas.arduino.config.vista;

import it.unibas.arduino.config.Costanti;
import it.unibas.arduino.config.modello.Pin;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModelloTabellaPin extends AbstractTableModel {

    private List<Pin> listPin = new ArrayList<Pin>();

    public ModelloTabellaPin(List<Pin> listPin) {
        this.listPin = listPin;
    }

    public int getRowCount() {
        if (this.listPin == null) {
            return 0;
        }
        return this.listPin.size();
    }

    public int getColumnCount() {
        return 7;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (listPin == null) {
            return null;
        }
        Pin pin = listPin.get(rowIndex);
        if (columnIndex == 0) {
            return pin.getId();
        }
        if (columnIndex == 1) {
            int pinNumber = pin.getPinNumber();
            if (pinNumber != -1) {
                return pinNumber;
            }
            return "N/A";
        }
        if (columnIndex == 2) {
            return Costanti.ADtype.get(pin.getAdType());
        }
        if (columnIndex == 3) {
            return Costanti.IOtype.get(pin.getIoType());
        }
        if (columnIndex == 4) {
            return pin.getDescrizione();
        }
        if (columnIndex == 5) {
            return pin.getModello();
        }
        if (columnIndex == 6) {
            return pin.getUm();
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "ID";
        }
        if (column == 1) {
            return "Pin Number";
        }
        if (column == 2) {
            return "A/D type";
        }
        if (column == 3) {
            return "I/O type";
        }
        if (column == 4) {
            return "Descrizione";
        }
        if (column == 5) {
            return "Modello";
        }
        if (column == 6) {
            return "UM";
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 1) {
            return Integer.class;
        }
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 2 || columnIndex == 3) {
            return false;
        }
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
        logger.info("Cambiato il valore della cella {}-{}: {}", rowIndex, columnIndex, aValue);
        Pin pin = listPin.get(rowIndex);
        if (columnIndex == 0) {
            pin.setId((String) aValue);
        }
        if (columnIndex == 1) {
            pin.setPinNumber((int) aValue);
        }
        if (columnIndex == 2) {
            pin.setAdType((int) aValue);
        }
        if (columnIndex == 3) {
            pin.setIoType((int) aValue);
        }
        if (columnIndex == 4) {
            pin.setDescrizione((String) aValue);
        }
        if (columnIndex == 5) {
            pin.setModello((String) aValue);
        }
        if (columnIndex == 6) {
            pin.setUm((String) aValue);
        }
    }

}
