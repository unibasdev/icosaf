package it.unibas.arduino.config.util;

import it.unibas.arduino.config.Applicazione;
import it.unibas.arduino.config.Costanti;
import it.unibas.arduino.client.Constants;
import it.unibas.arduino.client.model.ClientConfiguration;
import it.unibas.arduino.client.model.Command;
import it.unibas.arduino.client.model.PinValue;
import it.unibas.arduino.client.model.operator.ClientFactory;
import it.unibas.arduino.client.model.operator.CommandExecutionException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utility {

    public static Map<String, Integer> checkDeviceStatus() {
        Map<String, Integer> param = new HashMap<String, Integer>();
        try {
            Command commandRead = new Command(Constants.READ);
            PinValue pin0 = new PinValue("led_0");
            commandRead.addPinValue(pin0);
            PinValue pin1 = new PinValue("led_1");
            commandRead.addPinValue(pin1);
            PinValue pin2 = new PinValue("led_2");
            commandRead.addPinValue(pin2);
            PinValue pin3 = new PinValue("led_3");
            commandRead.addPinValue(pin3);
            PinValue pin4 = new PinValue("led_4");
            commandRead.addPinValue(pin4);
            PinValue pin5 = new PinValue("led_5");
            commandRead.addPinValue(pin5);
            PinValue temp = new PinValue("temp");
            commandRead.addPinValue(temp);
            ClientConfiguration clientConfiguration = (ClientConfiguration) Applicazione.getInstance().getModello().getBean(Costanti.CLIENT_CONF);
            ClientFactory.getInstance().getClient(clientConfiguration).execute(commandRead);

            param.put(pin0.getPinId(), pin0.getValue());
            param.put(pin1.getPinId(), pin1.getValue());
            param.put(pin2.getPinId(), pin2.getValue());
            param.put(pin3.getPinId(), pin3.getValue());
            param.put(pin4.getPinId(), pin4.getValue());
            param.put(pin5.getPinId(), pin5.getValue());
            param.put(temp.getPinId(), temp.getValue());
        } catch (CommandExecutionException ex) {
            Applicazione.getInstance().getFrame().mostraMessaggioErrore("Device not available");
        }
        return param;
    }

    public static String resolvingIPAddress(String hostname) {
        String ip = null;
        try {
            InetAddress address = InetAddress.getByName(hostname);
            ip = address.getHostAddress();
            logger.debug(hostname + " --|" + ip);
        } catch (UnknownHostException ex) {
            Applicazione.getInstance().getFrame().mostraMessaggioErrore("Unknown Hostname");
        }
        return ip;
    }

    public static ImageIcon caricaIcona(String nome) {
        URL urlIcona = Utility.class.getClassLoader().getResource(nome);
        if (urlIcona == null) {
            logger.warn("Impossibile trovare la risorsa {}", nome);
            return null;
        }
        return new ImageIcon(urlIcona);
    }
    
    public static boolean isStringaVuota(String s){
        return s == null || s.trim().isEmpty();
    }
}
