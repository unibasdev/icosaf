package it.unibas.arduino.client.model.operator;

import static it.unibas.arduino.client.enums.ClientMode.LOCAL;
import static it.unibas.arduino.client.enums.ClientMode.MQTT;
import static it.unibas.arduino.client.enums.ClientMode.PUB_NUB;
import it.unibas.arduino.client.model.ClientConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientFactory {

    private static ClientFactory singleton = new ClientFactory();

    public static ClientFactory getInstance() {
        return singleton;
    }

    private ClientFactory() {
    }

    public IClient getClient(ClientConfiguration conf) throws CommandExecutionException {
        logger.debug("Creating client for configuration {}", conf);
        if (conf.getConnectionMode().equals(MQTT)) {
            return new ClientMQTT(conf);
        } else if (conf.getConnectionMode().equals(LOCAL)) {
            return new ClientLocale(conf);
        } else if (conf.getConnectionMode().equals(PUB_NUB)) {
            return new ClientPubNub(conf);
        } else {
            logger.error("Configuration not supported");
            throw new IllegalArgumentException("Connection mode " + conf.getConnectionMode() + " is not supported.");
        }
    }

}
