package it.unibas.arduino.client.utils;

import it.unibas.arduino.client.Constants;
import it.unibas.arduino.client.model.PinValue;
import java.util.Arrays;
import java.util.List;

public class Utility {

    public static boolean checkType(String type, Long timer) {
        if (!Arrays.asList(Constants.COMMAND_TYPES).contains(type)) {
            return false;
        }
        if (type.equals(Constants.TIMED_WRITE) && timer == null) {
            return false;
        }
        if (!type.equals(Constants.TIMED_WRITE) && timer != null) {
            return false;
        }
        return true;
    }

    public static boolean checkPinValue(PinValue pinValue, String type) {
//        if (type.equals(Constants.TIMED_READ)) {
//            return false;
//        }
        if (pinValue.getPinId() == null) {
            return false;
        }
        if ((type.equals(Constants.WRITE) || type.equals(Constants.TIMED_WRITE)) && pinValue.getValue() == null) {
            return false;
        }
        if (type.equals(Constants.READ) && pinValue.getValue() != null) {
            return false;
        }
        return true;
    }

    public static boolean checkTimedPinValue(PinValue pinValue, String type) {
        if (pinValue.getPinId() == null) {
            return false;
        }
        if ((type.equals(Constants.WRITE) || type.equals(Constants.TIMED_WRITE)) && pinValue.getValue() == null) {
            return false;
        }
        return true;
    }

    public static void removeChars(int charsToRemove, StringBuilder result) {
        if (charsToRemove > result.length()) {
            throw new IllegalArgumentException("Unable to remove " + charsToRemove + " chars from a string with " + result.length() + " char!");
        }
        result.delete(result.length() - charsToRemove, result.length());
    }

    public static boolean containsSamePin(List<PinValue> pinValues, PinValue pinValue) {
        for (PinValue pin : pinValues) {
            if (pin.getPinId().equals(pinValue.getPinId())) {
                return true;
            }
        }
        return false;
    }

}
