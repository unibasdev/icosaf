package it.unibas.arduino.config;

import it.unibas.arduino.config.controllo.ControlloPrincipale;
import it.unibas.arduino.config.controllo.ControlloMonitor;
import it.unibas.arduino.config.modello.Configurazione;
import it.unibas.arduino.config.modello.Modello;
import it.unibas.arduino.config.modello.Monitor;
import it.unibas.arduino.config.persistenza.DAOArduinoConfigurazione;
import it.unibas.arduino.config.persistenza.DAOArduinoInfo;
import it.unibas.arduino.config.vista.PannelloConfigurazioneArduino;
import it.unibas.arduino.config.vista.Frame;
import it.unibas.arduino.config.vista.PannelloModificaStato;
import it.unibas.arduino.config.vista.PannelloMonitor;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Applicazione {

    private static Applicazione singleton = new Applicazione();

    public static Applicazione getInstance() {
        return singleton;
    }

    private Applicazione() {
    }

    private final Modello modello = new Modello();

    private final DAOArduinoConfigurazione daoArduinoConfigurazione = new DAOArduinoConfigurazione();
    private final DAOArduinoInfo daoArduinoInfo = new DAOArduinoInfo();

    private final ControlloPrincipale controlloConfigurazioneArduino = new ControlloPrincipale();
    private final ControlloMonitor controlloMonitor = new ControlloMonitor();

    private Frame frame;
    private PannelloConfigurazioneArduino pannelloConfigurazioneArduino;
    private PannelloMonitor pannelloMonitor;
    private PannelloModificaStato pannelloModificaStato;

    public void inizializza() {
        logger.info("Avviata applicazione");

        this.frame = new Frame();
        this.pannelloConfigurazioneArduino = new PannelloConfigurazioneArduino();
        this.pannelloMonitor = new PannelloMonitor(frame);
        this.pannelloModificaStato = new PannelloModificaStato(pannelloMonitor);

        this.pannelloConfigurazioneArduino.inizializza();
        this.frame.inizializza();
        this.pannelloMonitor.inizializza();
        this.frame.visualizza();
        this.modello.putBean(Costanti.ARDUINO_CONF, new Configurazione());
        this.modello.putBean(Costanti.HOSTNAME, Costanti.DEFAULT_HOSTNAME);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Applicazione.getInstance().inizializza();
            }
        });
    }

}
