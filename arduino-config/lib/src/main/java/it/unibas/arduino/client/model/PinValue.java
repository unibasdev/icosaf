package it.unibas.arduino.client.model;

public class PinValue {

    private String pinId;
    private Integer value;

    public PinValue(String sensorId) {
        this.pinId = sensorId;
    }

    public PinValue(String sensorId, Integer value) {
        this.pinId = sensorId;
        this.value = value;
    }

    public String getPinId() {
        return pinId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "PinValue{" + "pinId=" + pinId + ", value=" + value + '}';
    }

}
