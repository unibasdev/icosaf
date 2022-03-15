package it.unibas.arduino.config.controllo;

import it.unibas.arduino.client.Constants;
import it.unibas.arduino.client.model.ClientConfiguration;
import it.unibas.arduino.client.model.Command;
import it.unibas.arduino.client.model.PinValue;
import it.unibas.arduino.client.model.operator.ClientFactory;
import it.unibas.arduino.client.model.operator.CommandExecutionException;
import it.unibas.arduino.client.model.operator.IClient;
import it.unibas.arduino.config.Applicazione;
import it.unibas.arduino.config.Costanti;
import it.unibas.arduino.config.modello.Configurazione;
import it.unibas.arduino.config.modello.Monitor;
import static it.unibas.arduino.config.util.GestoreRisorse.getSVGIcon;
import it.unibas.arduino.config.vista.PannelloModificaStato;
import java.awt.event.ActionEvent;
import java.util.Map;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.JSpinner;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ControlloMonitor {

    private AzioneStartMonitor azioneStartMonitor = new AzioneStartMonitor();
    private AzioneStopMonitor azioneStopMonitor = new AzioneStopMonitor();
    private AzioneLeggiStato azioneLeggiStato = new AzioneLeggiStato();
    private AzioneModificaStato azioneModificaStato = new AzioneModificaStato();
    private AzioneSalvaNuovoStato azioneSalvaNuovoStato = new AzioneSalvaNuovoStato();

    public class AzioneStartMonitor extends AbstractAction {

        public AzioneStartMonitor() {
            this.putValue(NAME, "Avvia Monitor");
            this.putValue(SHORT_DESCRIPTION, "Avvia il monitoraggio del dispositivo");
            this.putValue(SMALL_ICON, getSVGIcon(Costanti.IMG_MONITOR_START));
        }

        public void actionPerformed(ActionEvent e) {
            ServizioMonitor servizioMonitor = new ServizioMonitor();
            servizioMonitor.start();
        }

    }

    public class AzioneStopMonitor extends AbstractAction {

        public AzioneStopMonitor() {
            this.putValue(NAME, "Interrompi Monitor");
            this.putValue(SHORT_DESCRIPTION, "Interrompi il monitoraggio del dispositivo");
            this.putValue(SMALL_ICON, getSVGIcon(Costanti.IMG_MONITOR_STOP));
        }

        public void actionPerformed(ActionEvent e) {
            Monitor monitor = (Monitor) Applicazione.getInstance().getModello().getBean(Costanti.MONITOR);
            monitor.setAttivo(false);
        }

    }

    public class AzioneLeggiStato extends AbstractAction {

        public AzioneLeggiStato() {
            this.putValue(NAME, "Leggi Stato");
            this.putValue(SHORT_DESCRIPTION, "Richiedi lo stato di tutti i sensori");
            this.putValue(SMALL_ICON, getSVGIcon(Costanti.IMG_MONITOR_READ));
        }

        public void actionPerformed(ActionEvent e) {
            ServizioMonitor servizioMonitor = new ServizioMonitor();
            servizioMonitor.leggiSingoloStato();
        }

    }

    public class AzioneModificaStato extends AbstractAction {

        public AzioneModificaStato() {
            this.putValue(NAME, "Cambia Stato");
            this.putValue(SHORT_DESCRIPTION, "Modifica lo stato degli attuatori");
            this.putValue(SMALL_ICON, getSVGIcon(Costanti.IMG_MONITOR_EDIT));
        }

        public void actionPerformed(ActionEvent e) {
            PannelloModificaStato pannelloModificaStato = Applicazione.getInstance().getPannelloModificaStato();
            pannelloModificaStato.inizializza();
            pannelloModificaStato.visualizza();
        }

    }

    public class AzioneSalvaNuovoStato extends AbstractAction {

        public AzioneSalvaNuovoStato() {
            this.putValue(NAME, "Invia Nuovo Stato");
            this.putValue(SHORT_DESCRIPTION, "Modifica lo stato degli attuatori");
            this.putValue(SMALL_ICON, getSVGIcon(Costanti.IMG_MONITOR_EDIT));
        }

        public void actionPerformed(ActionEvent e) {
            PannelloModificaStato pannelloModificaStato = Applicazione.getInstance().getPannelloModificaStato();
            pannelloModificaStato.setVisible(false);
            Monitor monitor = (Monitor) Applicazione.getInstance().getModello().getBean(Costanti.MONITOR);
            Configurazione config = monitor.getConfigurazione();
            if (config == null) {
                Applicazione.getInstance().getPannelloMonitor().mostraMessaggioErrore("Nessuna configurazione caricata");
                return;
            }
            ClientConfiguration clientConfiguration = new ClientConfiguration(config.getDeviceID(), config.getMqttServer(), config.getMqttPort(), config.getDevicePassword());
            IClient client;
            try {
                client = ClientFactory.getInstance().getClient(clientConfiguration);
            } catch (CommandExecutionException ex) {
                Applicazione.getInstance().getPannelloMonitor().mostraMessaggioErrore("Impossibile leggere la configurazione");
                return;
            }
            Map<String, JSpinner> mappaSpinner = (Map<String, JSpinner>) Applicazione.getInstance().getModello().getBean(Costanti.MAPPA_SPINNER);
            try {
                Command commandWrite = new Command(Constants.WRITE);
                for (String pinId : mappaSpinner.keySet()) {
                    commandWrite.addPinValue(new PinValue(pinId, (Integer) mappaSpinner.get(pinId).getValue()));
                }
                client.execute(commandWrite);
            } catch (Exception ex) {
                logger.error("Impossibile comunicare con il dispositivo", ex);
                Applicazione.getInstance().getPannelloMonitor().mostraMessaggioErrore("Impossibile comuicare con il dispositivo " + ex.getMessage());
            }
        }

    }

}
