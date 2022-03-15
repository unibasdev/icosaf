package it.unibas.arduino.client.model;

import it.unibas.arduino.client.utils.Utility;
import java.util.ArrayList;
import java.util.List;

public class Command {

    private String type;
    private List<PinValue> pinValues = new ArrayList<PinValue>();
    private List<PinValue> timedPinValues = new ArrayList<PinValue>();
    private Long timer;

    public Command(String type) {
        if (!Utility.checkType(type, null)) {
            throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
        this.type = type;
    }

    public Command(String type, long timer) {
        if (!Utility.checkType(type, timer)) {
            throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
        this.type = type;
        this.timer = timer;
    }

    public void addPinValue(PinValue pinValue) {
        if (!Utility.checkPinValue(pinValue, type)) {
            throw new IllegalArgumentException("Pin " + pinValue.toString() + " is not valid");
        }
        if (Utility.containsSamePin(pinValues, pinValue)) {
            throw new IllegalArgumentException("Pin " + pinValue.toString() + " is already present");
        }
        this.pinValues.add(pinValue);
    }

    public void addTimedPinValue(PinValue pinValue) {
        if (!Utility.checkTimedPinValue(pinValue, type)) {
            throw new IllegalArgumentException("Pin " + pinValue.toString() + " is not valid");
        }
        if (Utility.containsSamePin(timedPinValues, pinValue)) {
            throw new IllegalArgumentException("TimedPin " + pinValue.toString() + " is already present");
        }
        this.timedPinValues.add(pinValue);
    }

    public PinValue getPinValue(String pinId) {
        for (PinValue pinValue : pinValues) {
            if (pinValue.getPinId().equals(pinId)) {
                return pinValue;
            }
        }
        return null;
    }

    public PinValue getTimedPinValue(String pinId) {
        for (PinValue pinValue : timedPinValues) {
            if (pinValue.getPinId().equals(pinId)) {
                return pinValue;
            }
        }
        return null;
    }

    public List<PinValue> getTimedPinValues() {
        return timedPinValues;
    }

    public List<PinValue> getPinValues() {
        return pinValues;
    }

    public String getType() {
        return type;
    }

    public Long getTimer() {
        return timer;
    }

    public void setTimer(Long timer) {
        this.timer = timer;
    }

    @Override
    public String toString() {
        return "Command{" + "type=" + type + ", pinValues=" + pinValues + ", timer=" + timer + '}';
    }

}
