package it.unibas.arduino.config.modello;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Pin {

    private String id;
    private int pinNumber = -1;
    private int ioType;
    private int adType;
    private String descrizione;
    private String modello;
    private String um;

}
