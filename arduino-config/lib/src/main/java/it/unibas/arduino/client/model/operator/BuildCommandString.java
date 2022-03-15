package it.unibas.arduino.client.model.operator;

import it.unibas.arduino.client.Constants;
import it.unibas.arduino.client.utils.Utility;
import it.unibas.arduino.client.model.ClientConfiguration;
import it.unibas.arduino.client.model.Command;
import it.unibas.arduino.client.model.PinValue;

public class BuildCommandString {

    public String buildCommandString(Command command, ClientConfiguration configuration) {
        StringBuilder sb = new StringBuilder();
        String type = command.getType();
//        if (command.getType().equals(Constants.TIMED_READ)) {
//            type = Constants.READ;
//        }
        sb.append("/").append(type).append("/");
//        sb.append("/").append(Constants.LOCAL_ARDUINO_COMMAND).append("/").append(type);
        sb.append(configuration.getCommandSeparator());
        if (command.getType().equals(Constants.TIMED_WRITE)) {
            sb.append(Constants.TIMER_PIN);
            sb.append("=").append(command.getTimer());
            sb.append(Constants.PIN_SEPARATOR);
        }
//        if (command.getType().equals(Constants.TIMED_READ)) {
//            sb.append(Constants.TIMER_PIN);
//            sb.append(Constants.PIN_SEPARATOR);
//        }
        for (PinValue pinValue : command.getPinValues()) {
            sb.append(pinValue.getPinId());
            if (pinValue.getValue() != null) {
                sb.append("=").append(pinValue.getValue());
            }
            sb.append(Constants.PIN_SEPARATOR);
        }
        if (!command.getPinValues().isEmpty()) {
            Utility.removeChars(Constants.PIN_SEPARATOR.length(), sb);
        }
        return sb.toString();
    }

}
