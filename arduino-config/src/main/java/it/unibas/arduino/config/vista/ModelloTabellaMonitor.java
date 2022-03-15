package it.unibas.arduino.config.vista;

import it.unibas.arduino.config.modello.Monitor;
import it.unibas.arduino.config.modello.StatoSensori;
import java.text.DateFormat;
import static java.text.DateFormat.getDateTimeInstance;
import java.util.Date;
import static java.util.TimeZone.SHORT;
import javax.swing.table.AbstractTableModel;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ModelloTabellaMonitor extends AbstractTableModel {

    private static final DateFormat df = getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    private Monitor monitor;

    @Override
    public int getRowCount() {
        if (monitor == null) return 0;
        return monitor.getDati().size();
    }

    @Override
    public int getColumnCount() {
        return monitor.getConfigurazione().getListaSensori().size() + 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        StatoSensori stato = monitor.getDati().get(rowIndex);
        if (columnIndex == 0) {
            return df.format(stato.getTimestamp());
//            return stato.getTimestamp();
        }
        String idSensore = monitor.getConfigurazione().getListaSensori().get(columnIndex - 1).getId();
        return stato.getSensori().get(idSensore);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return (columnIndex == 0 ? String.class : Double.class);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return (columnIndex == 0 ? "Data/Ora" : monitor.getConfigurazione().getListaSensori().get(columnIndex - 1).getId());
    }

    public void aggiornaDati() {
        super.fireTableDataChanged();
    }

}
