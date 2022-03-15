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
import it.unibas.arduino.config.modello.Pin;
import it.unibas.arduino.config.modello.StatoSensori;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServizioMonitor extends Thread {

    @Override
    public void run() {
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
        monitor.setAttivo(true);
        aggiornaDati();
        while (monitor.isAttivo()) {
            try {
//            StatoSensori stato = generaStatoRandom(monitor);
                StatoSensori stato = leggiStato(monitor, client);
                monitor.getDati().add(0, stato);
                aggiornaDati();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                }
            } catch (CommandExecutionException ex) {
                logger.error("Impossibile comunicare con il dispositivo", ex);
                Applicazione.getInstance().getPannelloMonitor().mostraMessaggioErrore("Impossibile comuicare con il dispositivo " + ex.getMessage());
                monitor.setAttivo(false);
            }
        }
        aggiornaDati();
    }

    public void leggiSingoloStato() {
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
        try {
            StatoSensori stato = leggiStato(monitor, client);
            monitor.getDati().add(0, stato);
            aggiornaDati();
        } catch (CommandExecutionException ex) {
            logger.error("Impossibile comunicare con il dispositivo", ex);
            Applicazione.getInstance().getPannelloMonitor().mostraMessaggioErrore("Impossibile comuicare con il dispositivo " + ex.getMessage());
            monitor.setAttivo(false);
        }
    }

    private void aggiornaDati() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Applicazione.getInstance().getPannelloMonitor().aggiornaDati();
            }
        });
    }

    private StatoSensori generaStatoRandom(Monitor monitor) {
        StatoSensori statoSensori = new StatoSensori();
        statoSensori.setTimestamp(new Date());
        Map<String, Double> sensori = statoSensori.getSensori();
        for (Pin pin : monitor.getConfigurazione().getListPin()) {
            sensori.put(pin.getId(), new Random().nextDouble() * 100);
        }
        return statoSensori;
    }

    private StatoSensori leggiStato(Monitor monitor, IClient client) throws CommandExecutionException {
        Command commandRead = new Command(Constants.READ);
        for (Pin pin : monitor.getConfigurazione().getListPin()) {
            commandRead.addPinValue(new PinValue(pin.getId()));
        }
        client.execute(commandRead);
        StatoSensori statoSensori = new StatoSensori();
        statoSensori.setTimestamp(new Date());
        Map<String, Double> sensori = statoSensori.getSensori();
        for (PinValue pinValue : commandRead.getPinValues()) {
            Number n = pinValue.getValue();
            if (n == null) continue;
            sensori.put(pinValue.getPinId(), n.doubleValue());
        }
        return statoSensori;
    }

}
