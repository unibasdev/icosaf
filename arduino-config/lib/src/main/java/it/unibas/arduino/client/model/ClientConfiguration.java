package it.unibas.arduino.client.model;

import it.unibas.arduino.client.Constants;
import it.unibas.arduino.client.enums.ClientMode;
import static it.unibas.arduino.client.enums.ClientMode.LOCAL;
import static it.unibas.arduino.client.enums.ClientMode.MQTT;
import static it.unibas.arduino.client.enums.ClientMode.PUB_NUB;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ClientConfiguration {

    private String ipAddress;
    private String deviceID;
    private String mqttServer;
    private int mqttPort;
    private String password;
    private ClientMode connectionMode;

    public ClientConfiguration(String deviceID, String password) {
        this.deviceID = deviceID;
        this.password = password;
        this.connectionMode = PUB_NUB;
    }

    public ClientConfiguration(String deviceID, String mqttServer, int mqttPort, String password) {
        this.deviceID = deviceID;
        this.mqttServer = mqttServer;
        this.mqttPort = mqttPort;
        this.password = password;
        this.connectionMode = MQTT;
    }

    public ClientConfiguration(String deviceID, String password, ClientMode connectionMode) {
        this.deviceID = deviceID;
        this.password = password;
        this.connectionMode = connectionMode;
    }

    public ClientConfiguration(String ipAddress) {
        this.ipAddress = ipAddress;
        this.connectionMode = LOCAL;
    }

    public String getCommandSeparator() {
        if (connectionMode.equals(LOCAL)) {
            return Constants.LOCAL_COMMAND_SEPARATOR;
        }
        return Constants.INTERNET_COMMAND_SEPARATOR;
    }

}
