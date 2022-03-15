package it.unibas.arduino.config.controllo;

import it.unibas.arduino.config.Applicazione;
import it.unibas.arduino.config.Costanti;
import it.unibas.arduino.config.modello.Configurazione;
import it.unibas.arduino.config.modello.ArduinoInfo;
import it.unibas.arduino.config.modello.ArduinoSensorInfo;
import it.unibas.arduino.config.modello.Modello;
import it.unibas.arduino.config.modello.Monitor;
import it.unibas.arduino.config.modello.Pin;
import static it.unibas.arduino.config.util.GestoreRisorse.getSVGIcon;
import it.unibas.arduino.config.vista.PannelloConfigurazioneArduino;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toMap;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.YES_OPTION;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ControlloPrincipale {

    private AzioneComboIPStrategy azioneComboIpStrategy = new AzioneComboIPStrategy();
    private AzioneComboProtectionType actionComboProtectionType = new AzioneComboProtectionType();
    private AzioneComboADType actionComboADType = new AzioneComboADType();
    private AzioneAggiungiPin actionAddPin = new AzioneAggiungiPin();
    private AzioneCancellaPin actionDeletePIN = new AzioneCancellaPin();
    private AzioneMostraPassword azioneMostraPassword = new AzioneMostraPassword();
    private AzioneMostraDevicePassword azioneMostraDevicePassword = new AzioneMostraDevicePassword();
    private AzioneCaricaConfigurazione azioneCaricaConfigurazione = new AzioneCaricaConfigurazione();
    private AzioneSalvaConfigurazione azioneSalvaConfigurazione = new AzioneSalvaConfigurazione();
    private AzioneMonitor azioneMonitor = new AzioneMonitor();
    private AzioneEsci azioneEsci = new AzioneEsci();

    public class AzioneComboProtectionType implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            if (e.getItem().equals(Costanti.TYPE_PROTECTION.get(0))) {
                Applicazione.getInstance().getPannelloConfigurazioneArduino().hidePanelPw();
            } else {
                Applicazione.getInstance().getPannelloConfigurazioneArduino().visiblePanelPw();
            }
        }
    }

    public class AzioneComboIPStrategy implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            if (e.getItem().equals(Costanti.IP_STRATEGY.get(0))) {
                Applicazione.getInstance().getPannelloConfigurazioneArduino().hideIPManual();
            } else {
                Applicazione.getInstance().getPannelloConfigurazioneArduino().visibleIPManual();
            }
        }
    }

    public class AzioneComboADType implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
//            if (e.getItem().equals(Costanti.ADtype.get(2))) {
//                Applicazione.getInstance().getPannelloConfigurazioneArduino().enableRadioCode();
//            } else {
//                Applicazione.getInstance().getPannelloConfigurazioneArduino().disableRadioCode();
//            }
        }
    }

    public class AzioneAggiungiPin extends AbstractAction {

        public AzioneAggiungiPin() {
            this.putValue(NAME, "Aggiungi Pin");
            this.putValue(SHORT_DESCRIPTION, "Aggiungi un sensore o un attuatore");
            this.putValue(SMALL_ICON, getSVGIcon(Costanti.IMG_ADD));
        }

        public void actionPerformed(ActionEvent e) {
            PannelloConfigurazioneArduino panelConf = Applicazione.getInstance().getPannelloConfigurazioneArduino();
            Configurazione ac = (Configurazione) Applicazione.getInstance().getModello().getBean(Costanti.ARDUINO_CONF);
            String textPinID = panelConf.getTextPinID();
            String textDescrizione = panelConf.getTextDescrizione();
            String textModello = panelConf.getTextModello();
            String textUM = panelConf.getTextUM();
            if (textPinID.isEmpty()) {
                Applicazione.getInstance().getFrame().mostraMessaggioErrore("Il campo ID e' obbligatorio");
                return;
            }
            if (ac.isIDDuplicate(textPinID)) {
                Applicazione.getInstance().getFrame().mostraMessaggioErrore("Il valore ID specificato e' gia' utilizzato");
                return;
            }
            String comboAD = panelConf.getComboAD();
            String comboIO = panelConf.getComboIO();
            int spinnerPinNumber = panelConf.getSpinnerPinNumber();
            if (ac.isNumberDuplicate(spinnerPinNumber)) {
                Applicazione.getInstance().getFrame().mostraMessaggioErrore("Il Pin specificato e' gia' utilizzato");
                return;
            }
            Pin pin = creaNuovoPin(textPinID, comboAD, comboIO, textDescrizione, textModello, textUM);
            pin.setPinNumber(spinnerPinNumber);
            ac.addPIN(pin);
            panelConf.refreshTablePin(ac.getListPin());
        }

        public Pin creaNuovoPin(String textPinID, String comboAD, String comboIO, String textDescrizione, String textModello, String textUM) {
            Pin pin = new Pin();
            pin.setId(textPinID);
            pin.setAdType(Costanti.ADtype.indexOf(comboAD));
            pin.setIoType(Costanti.IOtype.indexOf(comboIO));
            pin.setDescrizione(textDescrizione);
            pin.setModello(textModello);
            pin.setUm(textUM);
            return pin;
        }
    }

    public class AzioneCancellaPin extends AbstractAction {

        public AzioneCancellaPin() {
            this.putValue(NAME, "Rimuovi Pin");
            this.putValue(SHORT_DESCRIPTION, "Rimuovi Pin ");
            this.putValue(SMALL_ICON, getSVGIcon(Costanti.IMG_REMOVE));
        }

        public void actionPerformed(ActionEvent e) {
            PannelloConfigurazioneArduino panelConf = Applicazione.getInstance().getPannelloConfigurazioneArduino();
            Configurazione ac = (Configurazione) Applicazione.getInstance().getModello().getBean(Costanti.ARDUINO_CONF);
            int[] selectedPin = panelConf.getSelectedPin();
            if (selectedPin == null) {
                Applicazione.getInstance().getFrame().mostraMessaggioErrore("Seleziona un pin dalla tabella");
                return;
            }
            List<Pin> listRemove = new ArrayList<Pin>();
            for (int t : selectedPin) {
                Pin get = ac.getListPin().get(t);
                listRemove.add(get);
            }
            for (Pin remove : listRemove) {
                ac.removePin(remove);
            }
            panelConf.refreshTablePin(ac.getListPin());
        }
    }

    public class AzioneMostraPassword implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Applicazione.getInstance().getPannelloConfigurazioneArduino().showPassword();
            } else {
                Applicazione.getInstance().getPannelloConfigurazioneArduino().hidePassword();
            }
        }
    }

    public class AzioneMostraDevicePassword implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Applicazione.getInstance().getPannelloConfigurazioneArduino().showDevicePassword();
            } else {
                Applicazione.getInstance().getPannelloConfigurazioneArduino().hideDevicePassword();
            }
        }
    }

    public class AzioneSalvaConfigurazione extends AbstractAction {

        public AzioneSalvaConfigurazione() {
            this.putValue(NAME, "Salva Configurazione");
            this.putValue(SHORT_DESCRIPTION, "Salva la Configurazione su File ");
            this.putValue(SMALL_ICON, getSVGIcon(Costanti.IMG_SAVE));
        }

        public void actionPerformed(ActionEvent e) {
            String error = validate();
            if (!error.isEmpty()) {
                Applicazione.getInstance().getFrame().mostraMessaggioErrore(error);
                return;
            }
            String pathFileConf = Applicazione.getInstance().getFrame().mostraSelezioneCartella();
            if (pathFileConf == null) {
                return;
            }
            logger.info("Selezionata cartella {}", pathFileConf);
            File fileConfig = new File(pathFileConf + File.separator + Costanti.CONFIG_FILE);
            if (fileConfig.exists()) {
                int risultato = Applicazione.getInstance().getFrame().mostraConfermaSovrascrittura();
                if (risultato != YES_OPTION) {
                    return;
                }
            }
            Configurazione ac = (Configurazione) Applicazione.getInstance().getModello().getBean(Costanti.ARDUINO_CONF);
            PannelloConfigurazioneArduino panelConf = Applicazione.getInstance().getPannelloConfigurazioneArduino();
            ac.setDeviceID(panelConf.getTextIDDevice());
            ac.setMqttServer(panelConf.getServerMQTT());
            ac.setMqttPort(panelConf.getPortMQTT());
            ac.setAutoReadTimer(panelConf.getAutoReadTimer());
            ac.setDevicePassword(panelConf.getTextDevicePassword());
            ac.setSSID(panelConf.getTextSSID());
            ac.setProtectionType(panelConf.getComboProtectionType());
            ac.setPassword(panelConf.getPassword());
            ac.setIPStrategy(panelConf.getComboIPStrategy() + 1);
            ac.setIp(formatAddress(panelConf.getTextIP()));
            ac.setGateway(formatAddress(panelConf.getTextGateway()));
            ac.setSubnet(formatAddress(panelConf.getTextSubnet()));
            ac.setDns(formatAddress(panelConf.getTextDNS()));
            try {
                Applicazione.getInstance().getDaoArduinoConfigurazione().salvaConfigurazione(fileConfig.toString(), ac);
                ArduinoInfo arduinoInfo = generaArduinoInfo(ac);
                File fileInfo = new File(pathFileConf + File.separator + Costanti.INFO_FILE);
                Applicazione.getInstance().getDaoArduinoInfo().salva(arduinoInfo, fileInfo.toString());
            } catch (Exception ex) {
                logger.error("Errore durante il salvataggio", ex);
                Applicazione.getInstance().getFrame().mostraMessaggioErrore("Errore durante il salvataggio del file. " + ex.getLocalizedMessage());
            }
            Applicazione.getInstance().getFrame().mostraMessaggioInformazioni("Configurazione salvata correttamente!");
        }

        private String validate() {
            PannelloConfigurazioneArduino panelConf = Applicazione.getInstance().getPannelloConfigurazioneArduino();

            StringBuilder error = new StringBuilder();
            if (panelConf.getTextSSID().isEmpty()) {
                error.append("Inserisci un SSID valido\n");
            }
            if (panelConf.getTextIDDevice().isEmpty()) {
                error.append("Inscerisci un Device ID valido\n");
            }
            if (panelConf.getComboProtectionType() != 0) {
                if (panelConf.getPassword().isEmpty()) {
                    error.append("Inserisci la password \n");
                }
            }
            if (panelConf.getComboIPStrategy() == 1) {
                if (!validateAddress(panelConf.getTextIP())) {
                    error.append("IP non valido\n");
                }
                if (!validateAddress(panelConf.getTextGateway())) {
                    error.append("Gateway non valido\n");
                }
                if (!validateAddress(panelConf.getTextSubnet())) {
                    error.append("Subnet non valido\n");
                }
                if (!validateAddress(panelConf.getTextDNS())) {
                    error.append("DNS non valido\n");
                }
            }
            return error.toString();
        }

        private boolean validateAddress(String[] textDNS) {
            for (String text : textDNS) {
                if (text.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        private String formatAddress(String[] textDNS) {
            StringBuilder builder = new StringBuilder();
            for (String text : textDNS) {
                builder.append(text);
                builder.append(".");
            }
            builder.deleteCharAt(builder.lastIndexOf("."));
            return builder.toString();
        }

        private ArduinoInfo generaArduinoInfo(Configurazione ac) {
            ArduinoInfo aInfo = new ArduinoInfo();
            for (Pin pin : ac.getListPin()) {
                aInfo.getSensors().add(ArduinoSensorInfo.builder()
                        .id(pin.getId())
                        .description(pin.getDescrizione())
                        .model(pin.getModello())
                        .um(pin.getUm())
                        .build());
            }
            return aInfo;
        }

    }

    public class AzioneCaricaConfigurazione extends AbstractAction {

        public AzioneCaricaConfigurazione() {
            this.putValue(NAME, "Apri Configurazione");
            this.putValue(SHORT_DESCRIPTION, "Carica la Configurazione da File ");
            this.putValue(SMALL_ICON, getSVGIcon(Costanti.IMG_OPEN));
        }

        public void actionPerformed(ActionEvent e) {
            String pathFileConf = Applicazione.getInstance().getFrame().mostraSelezioneCartella();
            if (pathFileConf == null) {
                return;
            }
            logger.info("Selezionata cartella {}", pathFileConf);
            File fileConfig = new File(pathFileConf + File.separator + Costanti.CONFIG_FILE);
            if (!fileConfig.exists()) {
                Applicazione.getInstance().getFrame().mostraMessaggioErrore("La cartella non contiene alcun file denominato " + Costanti.CONFIG_FILE);
                return;
            }
            Modello modello = Applicazione.getInstance().getModello();
            Configurazione ac;
            try {
                ac = Applicazione.getInstance().getDaoArduinoConfigurazione().caricaConfigurazione(fileConfig.toString());
                modello.putBean(Costanti.ARDUINO_CONF, ac);
                Applicazione.getInstance().getPannelloConfigurazioneArduino().aggiornaCampi();
            } catch (Exception ex) {
                Applicazione.getInstance().getFrame().mostraMessaggioErrore("Errore durante il caricamento del file " + fileConfig + ". Formato non corretto");
                return;
            }
            File fileInfo = new File(pathFileConf + File.separator + Costanti.INFO_FILE);
            if (fileInfo.exists()) {
                try {
                    ArduinoInfo info = Applicazione.getInstance().getDaoArduinoInfo().carica(fileInfo.toString());
                    aggiornaInformazioni(ac, info);
                    logger.debug("Caricate informazioni \n{}", info);
                } catch (Exception ex) {
                    logger.error("Errore durante il caricamento del file", ex);
                    Applicazione.getInstance().getFrame().mostraMessaggioErrore("Errore durante il caricamento del file " + fileInfo + ". Formato non corretto");
                }
            }
            Applicazione.getInstance().getFrame().mostraMessaggioInformazioni("Configurazione caricata correttamente!");
        }

        private void aggiornaInformazioni(Configurazione ac, ArduinoInfo info) {
            Map<String, Pin> mappaPin = ac.getListPin().stream()
                    .collect(toMap(Pin::getId, pin -> pin));
            for (ArduinoSensorInfo sensor : info.getSensors()) {
                Pin pin = mappaPin.get(sensor.getId());
                if (pin != null) {
                    pin.setDescrizione(sensor.getDescription());
                    pin.setModello(sensor.getModel());
                    pin.setUm(sensor.getUm());
                }
            }
        }

    }

    public class AzioneMonitor extends AbstractAction {

        public AzioneMonitor() {
            this.putValue(NAME, "Monitor");
            this.putValue(SHORT_DESCRIPTION, "Avvia il monitor del dispositivo");
            this.putValue(SMALL_ICON, getSVGIcon(Costanti.IMG_MONITOR));
        }

        public void actionPerformed(ActionEvent e) {
            Configurazione ac = (Configurazione) Applicazione.getInstance().getModello().getBean(Costanti.ARDUINO_CONF);
            Monitor monitor = new Monitor();
            monitor.setConfigurazione(ac);
            Applicazione.getInstance().getModello().putBean(Costanti.MONITOR, monitor);
            Applicazione.getInstance().getPannelloMonitor().visualizza();
        }
    }

    public class AzioneEsci extends AbstractAction {

        public AzioneEsci() {
            this.putValue(NAME, "Esci");
            this.putValue(SHORT_DESCRIPTION, "Esci dall'applicazione");
            this.putValue(SMALL_ICON, getSVGIcon(Costanti.IMG_EXIT));
        }

        public void actionPerformed(ActionEvent e) {
            int risultato = Applicazione.getInstance().getFrame().mostraConfermaUscita();
            if (risultato == JOptionPane.NO_OPTION) {
                return;
            }
            System.exit(0);
        }

    }

}
