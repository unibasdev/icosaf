package it.unibas.arduino.config.modello;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class ArduinoInfo {

    private List<ArduinoSensorInfo> sensors = new ArrayList<>();

}
