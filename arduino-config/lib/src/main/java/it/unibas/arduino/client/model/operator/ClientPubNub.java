package it.unibas.arduino.client.model.operator;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import it.unibas.arduino.client.Constants;
import it.unibas.arduino.client.model.ClientConfiguration;
import it.unibas.arduino.client.model.Command;
import java.util.Date;
import java.util.Random;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientPubNub implements IClient {

    private static Logger logger = LoggerFactory.getLogger(ClientPubNub.class);
    private BuildCommandString commandStringBuilder = new BuildCommandString();
    private ParseResponse responseParser = new ParseResponse();
    private Random random = new Random();
    private Pubnub pubnub;
    private ClientConfiguration configuration;

    private SubscribeCallback subscribeCallback = new SubscribeCallback();

    public ClientPubNub(ClientConfiguration configuration) throws CommandExecutionException {
        try {
            this.configuration = configuration;
            pubnub = new Pubnub(Constants.PUBNUB_PUB_KEY, Constants.PUBNUB_SUB_KEY, false);
            subscribeResponse();
        } catch (Exception e) {
            logger.error("Unable to create Internet Client. " + e.getLocalizedMessage());
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
        pubnub.unsubscribeAllChannels();
        pubnub.shutdown();
    }

    private String generateToken() {
        return "T" + random.nextInt(Constants.TOKEN_BOUND);
    }

    private void publishCommand(Command command, String token) throws CommandExecutionException {
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                if (logger.isDebugEnabled()) logger.debug("Success Callback: ", response.toString());
            }

            public void errorCallback(String channel, PubnubError error) {
                logger.error("Error publishing message. " + error.toString());
//                throw new RuntimeException("Error publishing message " + error.toString());
            }
        };
        String commandString = commandStringBuilder.buildCommandString(command, configuration);
        if (!command.getPinValues().isEmpty()) {
            commandString += "&";
        }
        commandString += Constants.TOKEN + "=" + token;
        commandString += "&";
        commandString += Constants.PASSWORD + "=" + configuration.getPassword();
        String channel = Constants.COMMAND_CHANNEL + configuration.getDeviceID();
        if (logger.isDebugEnabled()) logger.debug("Publishing command " + commandString + " on channel " + channel);
        try {
            pubnub.publish(channel, commandString, callback);
        } catch (Exception e) {
            logger.error("Unable to publish command. " + e.getLocalizedMessage());
            throw new CommandExecutionException("Unable to publish command. " + e.getLocalizedMessage());
        }
    }

    private void subscribeResponse() throws CommandExecutionException {
        try {
            String channel = Constants.RESPONSE_CHANNEL + configuration.getDeviceID();
            if (logger.isDebugEnabled()) logger.debug("Subscribing channel " + channel);
            pubnub.subscribe(new String[]{channel}, subscribeCallback);
        } catch (Exception e) {
            logger.error("Unable to get the response. " + e.getLocalizedMessage());
            throw new CommandExecutionException("Unable to get the response. " + e.getLocalizedMessage());
        }
    }

    class SubscribeCallback extends Callback {

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
        public void successCallback(String channel, Object message) {
            try {
                if (logger.isDebugEnabled()) logger.debug("Handling response message " + message);
                JSONObject jsonObject = (JSONObject) message;
                String messageToken = (String) jsonObject.get(Constants.TOKEN);
                if (!messageToken.equals(this.token)) {
                    return;
                }
                this.handled = true;
                String messageString = (String) jsonObject.toString();
                responseParser.parse(messageString, command);
            } catch (Exception ex) {
                logger.error("Error parsing message: " + ex.getLocalizedMessage());
                this.handled = true;
                this.exception = ex.getLocalizedMessage();
            }
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            logger.error("Error callback " + error.getErrorString());
            this.handled = true;
            this.exception = error.getErrorString();
        }

        @Override
        public void connectCallback(String channel, Object message) {
        }

        @Override
        public void disconnectCallback(String channel, Object message) {
        }

        public void reconnectCallback(String channel, Object message) {
        }
    };

}
