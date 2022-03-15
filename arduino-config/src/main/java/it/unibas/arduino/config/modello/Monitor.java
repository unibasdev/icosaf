package it.unibas.arduino.config.modello;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Monitor {

    private Configurazione configurazione;
    private boolean attivo;
    private List<StatoSensori> dati = new ArrayList<>();
    
}
