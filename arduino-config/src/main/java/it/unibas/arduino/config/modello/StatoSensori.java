package it.unibas.arduino.config.modello;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class StatoSensori {

    private Date timestamp;
    private Map<String, Double> sensori = new HashMap<>();
    
}
