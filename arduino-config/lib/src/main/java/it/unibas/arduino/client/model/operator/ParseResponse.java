package it.unibas.arduino.client.model.operator;

import com.google.gson.Gson;
import it.unibas.arduino.client.Constants;
import it.unibas.arduino.client.model.Command;
import it.unibas.arduino.client.model.PinValue;
import it.unibas.arduino.client.model.Response;
import it.unibas.arduino.client.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParseResponse {

    private final Gson gson;

    public ParseResponse() {
        this.gson = GsonUtil.getGson();
    }

    public void parse(String responseMessage, Command command) throws CommandExecutionException {
        logger.debug("Parsing message {}", responseMessage);
        if (responseMessage == null) {
            throw new CommandExecutionException("Wrong response");
        }
        Response response;
        try {
            response = gson.fromJson(responseMessage, Response.class);
        } catch (Exception ex) {
            logger.error("Error while parsing response {}", responseMessage, ex);
            throw new CommandExecutionException("Error while parsing response " + responseMessage);
        }
        handleErrorResponse(response);
        if (command.getType().equals(Constants.WRITE)) {
            handleWriteResponse(response);
            return;
        }
        if (command.getType().equals(Constants.TIMED_WRITE)) {
            handleTimedWriteResponse(response);
            return;
        }
        handleReadResponse(response, command);
        logger.debug("Parsing result: {}", command);
    }

    private void handleReadResponse(Response response, Command command) {
        logger.debug("Read message {}", response);
        for (String pinId : response.getData().keySet()) {
            String pinValueString = response.getData().get(pinId).trim();
            if (pinId.equals(Constants.DELAY_TIME)) {
                long longValue = Long.parseLong(pinValueString);
                command.setTimer(longValue);
                continue;
            }
            PinValue pinValue;
            if (pinId.startsWith(Constants.TIMED_PIN_PREFIX)) {
                String timedPinId = pinId.substring(Constants.TIMED_PIN_PREFIX.length());
                pinValue = new PinValue(timedPinId);
                command.addTimedPinValue(pinValue);
            } else {
                pinValue = command.getPinValue(pinId);
                if (pinValue == null) {
                    throw new IllegalArgumentException("Unknown pin " + pinId + " in response " + response);
                }
            }
            int value = Integer.parseInt(pinValueString);
            pinValue.setValue(value);
        }
    }

    private void handleWriteResponse(Response response) throws CommandExecutionException {
        if (!Constants.SUCCESS.equals(response.getResult())) {
            throw new CommandExecutionException("Error on device " + response);
        }
    }

    private void handleTimedWriteResponse(Response response) throws CommandExecutionException {
        if (!Constants.TIMED.equals(response.getResult())) {
            throw new CommandExecutionException("Error on device " + response);
        }
    }

    private void handleErrorResponse(Response response) throws CommandExecutionException {
        if (response.getError() != null) {
            throw new CommandExecutionException(response.getError());
        }
    }
}
