package it.unibas.arduino.config;

import java.util.Arrays;
import java.util.List;

public class Costanti {

    public static final String CONFIG_FILE = "config.txt";
    public static final String INFO_FILE = "sensor_info.json";

    public static final String DEFAULT_HOSTNAME = "icosaf_sensors";
    public static final String HOSTNAME = "HOSTNAME";

    public static final String IMG_APP = "app.svg";
    public static final String IMG_OPEN = "open.svg";
    public static final String IMG_SAVE = "save.svg";
    public static final String IMG_MONITOR = "monitor.svg";
    public static final String IMG_EXIT = "exit.svg";
    public static final String IMG_ADD = "add.svg";
    public static final String IMG_REMOVE = "remove.svg";
    public static final String IMG_MONITOR_START = "monitor-start.svg";
    public static final String IMG_MONITOR_STOP = "monitor-stop.svg";
    public static final String IMG_MONITOR_READ = "monitor-read.svg";
    public static final String IMG_MONITOR_EDIT = "monitor-edit.svg";

    public static final String IMG_REFRESH = "./varie/risorse/png/refresh.png";
    public static final String IMG_CONF = "./varie/risorse/png/conf.png";
    public static final String IMG_CONF_MENU = "./varie/risorse/png/confMenu.png";
    public static final String IMG_SYNC = "./varie/risorse/png/sync.png";
    public static final String IMG_HOSTNAME = "./varie/risorse/png/hostname.png";

    public static final String TEMP_READ = "TEMP_READ";
    public static final String PIN_READ = "PIN_READ";

    //modello
    public static final String ARDUINO_CONF = "ARDUINO_CONF";
    public static final String MONITOR = "MONITOR";
    public static final String CLIENT_CONF = "CLIENT_CONF";
    public static final String MAPPA_SPINNER = "MAPPA_SPINNER";
    public static final String IP_STRATEGY_DHCP = "DHCP";
    public static final String IP_STRATEGY_MANUAL = "Manuale";
    public static final List<String> TYPE_PROTECTION = Arrays.asList("NO Protection", "WEP", "WPA", "WPA2");
    public static final List<String> IP_STRATEGY = Arrays.asList(IP_STRATEGY_DHCP, IP_STRATEGY_MANUAL);
    public static final List<String> ADtype = Arrays.asList("Analog", "Digital");
    public static final List<String> IOtype = Arrays.asList("Input", "Output");

    public static final String CURRENT_PIN = "CURRENT_PIN";

}
