package it.unibas.arduino.config.modello;

import java.util.HashMap;
import java.util.Map;

public class Modello {
    
    private Map<String,Object> mapBean = new HashMap<String, Object>();
    
    public void putBean(String key, Object value){
        this.mapBean.put(key, value);
    }
    
    public Object getBean(String key){
        return this.mapBean.get(key);
    }
}
