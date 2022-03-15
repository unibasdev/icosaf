package it.unibas.arduino.config.vista;

import it.unibas.arduino.config.Applicazione;
import it.unibas.arduino.config.Costanti;
import it.unibas.arduino.config.modello.Monitor;
import it.unibas.arduino.config.modello.Pin;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

public class PannelloModificaStato extends JDialog {

    public PannelloModificaStato(Dialog parent) {
        super(parent, true);
    }

    public void inizializza() {
        Monitor monitor = (Monitor) Applicazione.getInstance().getModello().getBean(Costanti.MONITOR);
        JPanel pannelloSpinner = new JPanel();
        pannelloSpinner.setLayout(new GridLayout(0, 2));
        Map<String, JSpinner> componenti = new HashMap<>();
        for (Pin pin : monitor.getConfigurazione().getListaAttuatori()) {
            JSpinner spinner = new JSpinner();
            spinner.setModel(new SpinnerNumberModel(0, 0, 9999, 1));
            componenti.put(pin.getId(), spinner);
            pannelloSpinner.add(new JLabel(pin.getId()));
            pannelloSpinner.add(spinner);
        }
        JPanel pannello = new JPanel();
        pannello.setBorder(new TitledBorder("Nuovo stato attuatori"));
        pannello.setLayout(new BorderLayout());
        pannello.add(pannelloSpinner, BorderLayout.CENTER);
        pannello.add(new JButton(Applicazione.getInstance().getControlloMonitor().getAzioneSalvaNuovoStato()), BorderLayout.SOUTH);
        Applicazione.getInstance().getModello().putBean(Costanti.MAPPA_SPINNER, componenti);
        JScrollPane scrollPane = new JScrollPane(pannello);
        this.setContentPane(scrollPane);
    }

    public void visualizza() {
        this.setAlwaysOnTop(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

}
