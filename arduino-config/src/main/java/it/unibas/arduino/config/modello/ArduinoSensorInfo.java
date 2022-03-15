package it.unibas.arduino.config.modello;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ArduinoSensorInfo {

    private String id;
    private String description;
    private String model;
    private String um;
}
