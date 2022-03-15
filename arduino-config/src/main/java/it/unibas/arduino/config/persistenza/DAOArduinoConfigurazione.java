package it.unibas.arduino.config.persistenza;

import it.unibas.arduino.config.Costanti;
import static it.unibas.arduino.config.Costanti.IP_STRATEGY_MANUAL;
import it.unibas.arduino.config.modello.Configurazione;
import it.unibas.arduino.config.modello.Pin;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DAOArduinoConfigurazione {

    public Configurazione caricaConfigurazione(String path) throws IOException, DAOException {
        Configurazione conf = null;
        @Cleanup BufferedReader reader = null;
        logger.debug("Path file: {}", path);
        try {
            reader = new BufferedReader(new FileReader(path));
            conf = loadConfConnection(reader);
            logger.info("Configurazione caricata {}", conf);
            return conf;
        } catch (Exception ex) {
            logger.error("Errore durante il caricamento del file", ex);
            throw new DAOException(ex);
        }
    }

    private Configurazione loadConfConnection(BufferedReader reader) throws IOException {
        Configurazione arduinoConfiguration = new Configurazione();
        String line = reader.readLine();
        while (!line.startsWith("--")) {
            assignField(line, arduinoConfiguration);
            line = reader.readLine();
        }
        line = reader.readLine();
        logger.debug("Linea: {}", line);
        while (line != null) {
            Pin pin = new Pin();
            for (int i = 0; i < 4; i++) {
                assignPinField(line, pin);
                line = reader.readLine();
            }
            arduinoConfiguration.addPIN(pin);
        }

        return arduinoConfiguration;
    }

    private void assignField(String line, Configurazione arduinoConfiguration) {
        String[] split = line.split(":");
        String key = split[0].trim();
        String value = split[1].trim();
        logger.debug("{}-{}", key, value);
        if (key.equals("deviceId")) {
            arduinoConfiguration.setDeviceID(value.trim());
        }
        if (key.equals("ssid")) {
            arduinoConfiguration.setSSID(value.trim());
        }
        if (key.equals("protectionType")) {
            arduinoConfiguration.setProtectionType(Integer.parseInt(value.trim()));
        }
        if (key.equals("wifiPassword")) {
            arduinoConfiguration.setPassword(value.trim());
        }
        if (key.equals("ipStrategy")) {
            arduinoConfiguration.setIPStrategy(Integer.parseInt(value.trim()));
        }
        if (key.equals("ip")) {
            arduinoConfiguration.setIp(value.trim());
        }
        if (key.equals("gateway")) {
            arduinoConfiguration.setGateway(value.trim());
        }
        if (key.equals("subnet")) {
            arduinoConfiguration.setSubnet(value.trim());
        }
        if (key.equals("dns")) {
            arduinoConfiguration.setDns(value.trim());
        }
        if (key.equals("mqttServer")) {
            String server = value.trim();
            server = inverti(server);
            arduinoConfiguration.setMqttServer(server);
        }
        if (key.equals("mqttPort")) {
            arduinoConfiguration.setMqttPort(Integer.parseInt(value.trim()));
        }
        if (key.equals("autoReadTimer")) {
            arduinoConfiguration.setAutoReadTimer(Integer.parseInt(value.trim()));
        }
        if (key.equals("devicePassword")) {
            arduinoConfiguration.setDevicePassword(value.trim());
        }
    }

    private void assignPinField(String line, Pin pin) {
        String[] split = line.split(": ");
        String key = split[0];
        String value = split[1];
        if (key.startsWith("pinNumber")) {
            pin.setPinNumber(Integer.parseInt(value));
        }
        if (key.startsWith("ioType")) {
            pin.setIoType(Integer.parseInt(value));
        }
        if (key.startsWith("adType")) {
            pin.setAdType(Integer.parseInt(value));
        }
        if (key.startsWith("id")) {
            pin.setId(value);
        }
    }

    public void salvaConfigurazione(String path, Configurazione ac) throws DAOException, IOException {
        logger.debug("Salvataggio {}", path);
        @Cleanup FileWriter writer = null;
        try {
            writer = new FileWriter(new File(path));
            BufferedWriter bw = new BufferedWriter(writer);
            StringBuilder builder = new StringBuilder();
            builder.append("deviceId: ").append(ac.getDeviceID()).append("\n");
            builder.append("debug: ").append(ac.getDebug()).append("\n");
            builder.append("sound: ").append(ac.getSound()).append("\n");
            builder.append("devicePassword: ").append(ac.getDevicePassword()).append("\n");
            builder.append("ssid: ").append(ac.getSSID()).append("\n");
            builder.append("protectionType: ").append(ac.getProtectionType()).append("\n");
            builder.append("wifiPassword: ").append(ac.getPassword()).append("\n");
            builder.append("ipStrategy: ").append(ac.getIPStrategy()).append("\n");
            if (ac.getIPStrategy() == Costanti.IP_STRATEGY.indexOf(IP_STRATEGY_MANUAL)) {
                builder.append("ip: ").append(ac.getIp()).append("\n");
                builder.append("gateway: ").append(ac.getGateway()).append("\n");
                builder.append("subnet: ").append(ac.getSubnet()).append("\n");
                builder.append("dns: ").append(ac.getDns()).append("\n");
            }
            builder.append("mqttServer: ").append(inverti(ac.getMqttServer())).append("\n");
            builder.append("mqttPort: ").append(ac.getMqttPort()).append("\n");
            builder.append("autoReadTimer: ").append(ac.getAutoReadTimer()).append("\n");
            builder.append("--pins--").append("\n");
            for (Pin pin : ac.getListPin()) {
                builder.append("id: ").append(pin.getId()).append("\n");
                builder.append("pinNumber: ").append(pin.getPinNumber()).append("\n");
                builder.append("ioType: ").append(pin.getIoType()).append("\n");
                builder.append("adType: ").append(pin.getAdType()).append("\n");
            }
            bw.write(builder.toString());
            bw.flush();
        } catch (IOException ex) {
            logger.error("Errore durante il salvataggio del file", ex);
            throw new DAOException(ex);
        }

    }

    private String inverti(String server) {
        if (server == null) {
            return server;
        }
        String stringaPatternIP
                = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern ipPattern = Pattern.compile(stringaPatternIP);
        Matcher matcher = ipPattern.matcher(server);
        if (!matcher.matches()) {
            return server;
        }
        return matcher.group(4) + "." + matcher.group(3) + "." + matcher.group(2) + "." + matcher.group(1);
    }
}
