package it.unibas.arduino.config.modello;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Configurazione {
    
    private String deviceID;
    private String mqttServer;
    private int mqttPort;
    private int autoReadTimer;
    private String devicePassword;
    
    private String SSID;
    private int protectionType;
    private String password;
    private int IPStrategy;
    private String ip;
    private String gateway;
    private String subnet;
    private String dns;
    
    private int debug = 0;
    private int sound = 0;
    
    private List<Pin> listPin = new ArrayList<Pin>();
    
    public boolean isNumberDuplicate(int pinNumber) {
        for (Pin pin : listPin) {
            if (pin.getPinNumber() == pinNumber) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isIDDuplicate(String id) {
        for (Pin pin : listPin) {
            if (pin.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
    
    public void addPIN(Pin pin) {
        this.listPin.add(pin);
    }
    
    public void removePin(Pin pin) {
        this.listPin.remove(pin);
    }
    
    public List<Pin> getListaSensori() {
        return this.listPin.stream().filter(s -> s.getIoType() == 0).sorted((s1, s2) -> s1.getId().compareTo(s2.getId())).collect(Collectors.toList());
    }
    
    public List<Pin> getListaAttuatori() {
        return this.listPin.stream().filter(s -> s.getIoType() == 1).sorted((s1, s2) -> s1.getId().compareTo(s2.getId())).collect(Collectors.toList());
    }
    
}
