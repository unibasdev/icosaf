package it.unibas.arduino.client.model.operator;

import it.unibas.arduino.client.Constants;
import static it.unibas.arduino.client.Constants.COMMAND_CHANNEL;
import it.unibas.arduino.client.model.ClientConfiguration;
import it.unibas.arduino.client.model.Command;
import java.util.Date;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

@Slf4j
public class ClientMQTT implements IClient {

    private BuildCommandString commandStringBuilder = new BuildCommandString();
    private ParseResponse responseParser = new ParseResponse();
    private Random random = new Random();
    private ClientConfiguration configuration;
    private MqttClient mqttClient;
    private int qos = 0;
    private SubscribeCallback subscribeCallback = new SubscribeCallback();

    public ClientMQTT(ClientConfiguration configuration) throws CommandExecutionException {
        try {
            this.configuration = configuration;
            String broker = "tcp://" + configuration.getMqttServer() + ":" + configuration.getMqttPort();
            String clientId = "arduino-library." + new Random().nextInt();
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            logger.debug("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            logger.debug("Client connected");
            subscribeResponse();
        } catch (Exception e) {
            logger.error("Unable to create Internet Client. " + e.getLocalizedMessage(), e);
            throw new CommandExecutionException("Unable to create Internet Client. " + e.getLocalizedMessage());
        }
    }

    public void execute(Command command) throws CommandExecutionException {
        String token = generateToken();
        subscribeCallback.setHandled(false);
        subscribeCallback.setException(null);
        subscribeCallback.setCommand(command);
        subscribeCallback.setToken(token);
        publishCommand(command, token);
        long start = new Date().getTime();
        for (int i = 0; i < Constants.TIMEOUT_ITERATIONS; i++) {
            try {
                Thread.sleep(Constants.CHECK_INTERVAL);
            } catch (InterruptedException ex) {
            }
            if (subscribeCallback.isHandled()) {
                if (subscribeCallback.getException() != null) {
                    logger.error("Executing exception " + subscribeCallback.getException());
                    throw new CommandExecutionException(subscribeCallback.getException());
                }
                return;
            }
        }
        long end = new Date().getTime();
        throw new CommandExecutionException("Timeout expired for command " + command + " - Elapsed time: " + (end - start) + " ms");
    }

    public void close() {
        try {
            mqttClient.disconnect();
        } catch (MqttException ex) {
            logger.warn("Unable to disconnect", ex);
        }
    }

    private String generateToken() {
        return "T" + random.nextInt(Constants.TOKEN_BOUND);
    }

    private void publishCommand(Command command, String token) throws CommandExecutionException {
        String commandString = commandStringBuilder.buildCommandString(command, configuration);
        if (!command.getPinValues().isEmpty()) {
            commandString += "&";
        }
        commandString += Constants.TOKEN + "=" + token;
        commandString += "&";
        commandString += Constants.PASSWORD + "=" + configuration.getPassword();
        try {
            logger.debug("Publishing command {} on channel {}", commandString, Constants.COMMAND_CHANNEL);
            MqttMessage message = new MqttMessage(commandString.getBytes());
            message.setQos(qos);
            mqttClient.publish(COMMAND_CHANNEL, message);
            logger.debug("Command published");
        } catch (Exception ex) {
            logger.error("Unable to publish command. " + ex.getLocalizedMessage(), ex);
            throw new CommandExecutionException("Unable to publish command. " + ex.getLocalizedMessage());
        }
    }

    private void subscribeResponse() throws CommandExecutionException {
        try {
            logger.debug("Subscribing channel " + Constants.RESPONSE_CHANNEL);
            mqttClient.subscribe(Constants.RESPONSE_CHANNEL, subscribeCallback);
        } catch (Exception ex) {
            logger.error("Unable to get the response. " + ex.getLocalizedMessage(), ex);
            throw new CommandExecutionException("Unable to get the response. " + ex.getLocalizedMessage());
        }
    }

    class SubscribeCallback implements IMqttMessageListener {

        private boolean handled;
        private String exception;
        private Command command;
        private String token;

        public boolean isHandled() {
            return handled;
        }

        public void setHandled(boolean handled) {
            this.handled = handled;
        }

        public void setCommand(Command command) {
            this.command = command;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getException() {
            return exception;
        }

        public void setException(String exception) {
            this.exception = exception;
        }

        @Override
        public void messageArrived(String topic, MqttMessage mm) throws Exception {
            try {
                logger.debug("Topic {} - Received message: {}", topic, mm);
                JSONObject jsonObject = new JSONObject(mm.toString());
                String messageToken = (String) jsonObject.get(Constants.TOKEN);
                if (!messageToken.equals(this.token)) {
                    return;
                }
                this.handled = true;
                String messageString = jsonObject.toString();
                responseParser.parse(messageString, command);
            } catch (JSONException ex) {
                logger.warn("Skipping unexpected message: {}", mm, ex);
            } catch (Exception ex) {
                logger.error("Error parsing message: " + ex.getLocalizedMessage(), ex);
                this.handled = true;
                this.exception = ex.getLocalizedMessage();
            }
        }

    };

}
