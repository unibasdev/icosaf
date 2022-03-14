#include <SPI.h>
#include <LinkedList.h>
#include <MemoryFree.h>
#include <TimerThree.h>

#include <PubSubClient.h>
//#include <ccspi.h>
#include <ArduinoJson.h>

#include <Fishino.h>
#include "FishinoSdFat.h"
#include <FishinoRTC.h>

#define NOTE_C4      262
#define NOTE_G4      392
#define NOTE_C5      523
#define NOTE_D5      587
#define NOTE_E5      659
#define NOTE_A5      880
#define NOTE_B5      988
#define NOTE_SUSTAIN 50

////////////////////////////////////////////////////////////////
///////                 CONSTANTS                   ////////////
////////////////////////////////////////////////////////////////
#define MICROSECONDS            1000000
#define TRUE                    1
#define FALSE                   0
#define NULL                    0
#define SPEAKER_PIN             8
#define ATTEMPT                 3
#define ON                      1
#define OFF                     0
// SD
#define SD_PIN                  4
#define SD_OUTPUT_PIN           53
// Configuration
#define CONF_FILE               "icosaf.txt"
#define NO_PROTECTION           0
#define WEP                     1
#define WPA                     2
#define WPA2                    3
#define PIN_STATUS_LED          2
#define PIN_STATUS_OK_LED       3
#define PIN_STATUS_KO_LED       6
#define PIN_MQTT_LED            7
#define SYSTEMSTATUS_BLINK_TIME 150
#define MQTT_BLINK_TIME         150
//#define DHCP                    1
//#define MANUAL                  2

#define IPADDR      172, 29, 2, 150
#define GATEWAY     172, 29, 2, 130
#define NETMASK     255, 255, 255, 0

#define INPUT_TYPE              0
#define OUTPUT_TYPE             1
#define ANALOG_TYPE             0
#define DIGITAL_TYPE            1
#define ANALOG_AS_DIGITAL       2
#define ANALOG_AS_DIGITAL_TSD   200
// ULTRASOUND SENSOR
#define TRIGGER_PIN             12
#define SENSOR_TYPE_RAW         0
#define SENSOR_TYPE_ULTRASOUND  1
// WIFI
//---#define LISTEN_PORT             80
#define MAX_ACTION              10
#define MAX_PATH                512
//---#define BUFFER_SIZE             MAX_ACTION + MAX_PATH + 20
#define STRING_BUFFER_SIZE      512
//---#define TIMEOUT_MS              500
//#define WLAN_SECURITY   WLAN_SEC_WPA2
#define IDLE_TIMEOUT_MS         3000
// SYSTEM
#define SYSTEM_STATUS_OK        1
#define SYSTEM_STATUS_KO        0
// OUTPUT
#define IN_VALUE_SEPARATOR      ','
//#define LOCAL_SEPARATOR         '-'
#define SEPARATOR               '-'
#define DELAY_TIME              "delayTime"
#define TIMED_PIN_PREFIX        "timed_"
// RESULTS
#define OK                      "OK"
//#define TIME_LOOPSENSORVALUES   5000


////////////////////////////////////////////////////////////////
///////                    TYPES                    ////////////
////////////////////////////////////////////////////////////////
struct pinConfiguration {
    String id;              // Must be unique
    unsigned int pinNumber;
    boolean ioType;
    int adType;
    int sensorType;
};

struct configuration {
    boolean debug;
    //boolean local;
    boolean sound;
    // *sezione WiFi
    byte mac[6];
    String ssid;
    unsigned int protectionType;
    String wifiPassword;
    unsigned int ipStrategy;
    byte ip[4];
    byte gateway[4];
    byte subnet[4];
    byte dns[4];
    // *sezione PubNub
    //char pubkey[43];
    //char subkey[43];
    IPAddress mqttServer;
    int mqttPort;
    String devicePassword;
    //*sezione dispositivo
    String id;
    unsigned int port;
    int autoReadTimer;
    //*sezione pin
    LinkedList<pinConfiguration> pins;
};

struct queryStringCommand {
    String pinId;
    unsigned int pinNumber;
    boolean ioType;
    int adType;
    int sensorType;
    unsigned long value;
};

struct pinStatus {
    String pinId;
    unsigned int pinNumber;
    boolean ioType;
    int adType;
    int sensorType;
    int value;
};

// REQUEST EXAMPLES
// ----------------
// /timedWrite/-timer=10&led1=1&led2=1&led3=1&led4=1&led5=1&token=T0001&password=((icosaf))
// /read/-s1&s2&s3&s4&s5&token=T0001&password=((icosaf))
// /write/-led1=1&led2=1&led3=1&led4=1&led5=1&token=T0001&password=((icosaf))

LinkedList<queryStringCommand> extractQueryStringCommands(String queryString, LinkedList<String> outcomes);
LinkedList<String> splitString(String string, String separator);
LinkedList<String> splitCommands(char* commands);
LinkedList<String> splitCommands(char* commands);
String removeCharsBeforeString(String string, String val);
void printPin(pinConfiguration pin);
String executeOutputCommand(queryStringCommand command);
JsonObject handleInput(String queryString);
LinkedList<pinStatus> extractQueryStringSensors(String queryString, LinkedList<String> outcomes);
int readSensorValue(pinStatus sensor);
void executeTimedOutputCommands();
void parseIp(String value, byte ip[4]);

//IPAddress mqttServer(188,55,216,54);   // Donatello's mosquitto
////IPAddress mqttServer(18,43,168,192); // local mosquitto (phone network)
////IPAddress mqttServer(5,1,168,192);   // local mosquitto (home network)
//// N.B. With CC3000 Reverse IP address !
FishinoClient fishinoClient;
PubSubClient pubsubClient(fishinoClient);

unsigned long currentMillisAutoReadTimer;
unsigned long lastTimeMillisAutoReadTimer = 0;
unsigned long currentMillisMQTTLed;
unsigned long lastTimeMillisMQTTLed = 0;
long currentMillisSystemStatusLed;
long lastTimeMillisSystemStatusLed = 0;
int statusLed = OFF;
int systemStatus = SYSTEM_STATUS_OK;
int periodicTokenCounter = 1;
int messageBeep = OFF;

////////////////////////////////////////////////////////////////
///////                    SETUP                    ////////////
////////////////////////////////////////////////////////////////
configuration conf;
char path[MAX_PATH+1];
char stringBuffer[STRING_BUFFER_SIZE];
int ledStatus = 0;

volatile boolean timerActive = FALSE;
volatile unsigned long delayTime = 0;
LinkedList<queryStringCommand> timedCommands;
char commandTopic[50];
char responseTopic[50];

void setup() {
    poweronSignal();
    Serial.begin(115200);
//    Serial.begin(9600);
    initializeStatusLed();
    loadConfiguration();
    configurePin();
    printPinConfiguration();
    initializeWiFi();
    initializeClient();
    initializeChannels();
    initializeTimer();
    initializeRTC();
    Serial.print(F("\nInitial free memory: "));
    Serial.println(freeMemory());
}

void initializeStatusLed() {
    pinMode(PIN_STATUS_OK_LED,OUTPUT);
    pinMode(PIN_STATUS_LED,OUTPUT);
    pinMode(PIN_STATUS_KO_LED,OUTPUT);
    pinMode(PIN_MQTT_LED,OUTPUT);
    digitalWrite(PIN_STATUS_OK_LED,LOW);
    digitalWrite(PIN_STATUS_LED,HIGH);
    digitalWrite(PIN_STATUS_KO_LED,LOW);
    digitalWrite(PIN_MQTT_LED,LOW);
}

void initializeWiFi(){
    if (systemStatus == SYSTEM_STATUS_KO) {
        systemStatusError();
        return;
    }

    while(!Fishino.reset()) {
        Serial << F("Fishino RESET FAILED, RETRYING...\n");
    }
    Serial << F("Fishino WiFi RESET OK\n");
    Fishino.setPhyMode(PHY_MODE_11N);
    Fishino.setMode(STATION_MODE);
    Serial.print(F("\nAttempting to connect to ")); Serial.println(conf.ssid);
    const char* wifiSSID = conf.ssid.c_str();
    const char* wifiPWD = conf.wifiPassword.c_str();
    while(!Fishino.begin(wifiSSID, wifiPWD)) { // [Michele] Set maximum number of attempts
        Serial << ".";
        delay(500);
    }

    Serial.println(F("Connected!"));
    //Serial.println(F("Request DHCP"));
    //Fishino.staStartDHCP();
    Serial.print(F("Waiting for IP..."));
    IPAddress ip(IPADDR);
    IPAddress gw(GATEWAY);
    IPAddress nm(NETMASK);
    Fishino.config(ip, gw, nm);

    while(Fishino.status() != STATION_GOT_IP) { // [Michele] Set maximum number of attempts
        Serial << ".";
        delay(500);
    }

    if(Fishino.status() == STATION_GOT_IP) {
        Serial.println(F("OK"));
        wifiConnectionSuccess();
        printWifiStatus();

        stringToCharArray(conf.id, stringBuffer);
        Serial.println(stringBuffer);
    } else {
        Serial.println(F("Failed!"));
        wifiConnectionError();
    }
}

void startTimer(unsigned long seconds){
    if(conf.debug) Serial.print(F("Setting time to "));
    if(conf.debug) Serial.print(seconds);
    if(conf.debug) Serial.println(F(" seconds"));
    timerActive = true;
    delayTime = seconds;
    Timer3.start();
}

void stopTimer(){
    Timer3.stop();
    timerActive = false;
    delayTime = 0;
    timedCommands.clear();
}

void stringToCharArray(String s, char* buffer){
    s.toCharArray(buffer, s.length() + 1);
}

void printWifiStatus() {
    if(conf.debug) printDebug(F("SSID: "));
    if(conf.debug) printlnDebug(Fishino.SSID());

    IPAddress ip = Fishino.localIP();
    if(conf.debug) printDebug(F("IP Address: "));
    if(conf.debug) printDebug(ip[0]);
    if(conf.debug) printDebug(F("."));
    if(conf.debug) printDebug(ip[1]);
    if(conf.debug) printDebug(F("."));
    if(conf.debug) printDebug(ip[2]);
    if(conf.debug) printDebug(F("."));
    if(conf.debug) printlnDebug(ip[3]);

    long rssi = Fishino.RSSI();
    if(conf.debug) printDebug(F("signal strength (RSSI):"));
    if(conf.debug) printDebug(rssi);
    if(conf.debug) printlnDebug(F(" dBm\n"));
}

void configurePin() {
    if (systemStatus == SYSTEM_STATUS_KO) {
        systemStatusError();
        return;
    }

    for (int i = 0;i < conf.pins.size();i++) {
        pinConfiguration pin = conf.pins.get(i);
        if (pin.sensorType == SENSOR_TYPE_RAW) {
            configureRawPin(pin);
        } else if (pin.sensorType == SENSOR_TYPE_ULTRASOUND) {
            configureUltrasoundInputPin(pin);
        } else {
            Serial.print(F("Wrong sensor type for pin: "));
            Serial.println(pin.pinNumber); 
        }
    }
}

void configureRawPin(pinConfiguration pin) {
    if (pin.ioType == INPUT_TYPE) {
        pinMode(pin.pinNumber, INPUT);
    } else if (pin.ioType == OUTPUT_TYPE) {
        pinMode(pin.pinNumber, OUTPUT);
    } else {
        Serial.print(F("Wrong IO type for pin: "));
        Serial.println(pin.ioType); 
    }
}

void configureUltrasoundInputPin(pinConfiguration pin) {
    if (pin.ioType != INPUT_TYPE) {
        Serial.print(F("Wrong IO type for pin: "));
        Serial.println(pin.pinNumber);
    } else {
        pinMode(TRIGGER_PIN, OUTPUT);
        pinMode(pin.pinNumber, INPUT);
    }
}

void initializeHttp(){
    if(conf.debug) Serial.println(F("Listening for connections..."));
}

void initializeClient() {
    if (systemStatus == SYSTEM_STATUS_KO) {
        systemStatusError();
        return;
    }

    //pubsubClient.setServer(mqttServer, 1883);
    pubsubClient.setServer(conf.mqttServer, conf.mqttPort);
    pubsubClient.setCallback(handleMQTTRequestCallback);
}

void initializeChannels(){
    if (systemStatus == SYSTEM_STATUS_KO) {
        systemStatusError();
        return;
    }

    //stringToCharArray(conf.id + "_command", commandTopic);
    //stringToCharArray(conf.id + "_data", responseTopic);
    stringToCharArray("command_data", commandTopic);
    stringToCharArray("sensors_data", responseTopic);
    if(conf.debug) printDebug(F("Command topic: "));
    if(conf.debug) printlnDebug(commandTopic);
    if(conf.debug) printDebug(F("Response topic: "));
    if(conf.debug) printlnDebug(responseTopic);
}

void initializeTimer(){
    if (systemStatus == SYSTEM_STATUS_KO) {
        systemStatusError();
        return;
    }

    timedCommands = LinkedList<queryStringCommand>();
    Timer3.initialize(1 * MICROSECONDS); //Every second
    //stopTimer();
    Timer3.attachInterrupt(executeTimedOutputCommands); 
}

void initializeRTC() {
    if (systemStatus == SYSTEM_STATUS_KO) {
        systemStatusError();
        return;
    }

    if (!RTC.isrunning()) {
        Serial.println(F("\nRTC is NOT running!\n"));
    }
}

////////////////////////////////////////////////////////////////
///////                    LOOP                     ////////////
////////////////////////////////////////////////////////////////

void loop() {
    if (systemStatus == SYSTEM_STATUS_KO) {
        systemStatusError();
        return;
    }

    checkWiFiStatus();
    loopMQTT();
    timedLoopSensorValues();
}

void checkWiFiStatus() {
    if (Fishino.status() != STATION_GOT_IP) {
        Serial.print(F("WiFi status error: "));
        Serial.println(Fishino.status());
        initializeWiFi();
    }
}

////////////////////////////////////////////////////////////////
///////                   PUBNUB                    ////////////
////////////////////////////////////////////////////////////////

void loopMQTT(){
    while (!pubsubClient.connected()) {
        if(conf.debug) printDebug(F("Attempting MQTT connection..."));
        Serial.println("Attempting MQTT connection...");
        // Attempt to connect
        if (pubsubClient.connect("icosaf-arduinoDevice")) {
            if(conf.debug) printlnDebug(F("Connected to MQTT broker"));
            Serial.println("Connected to MQTT broker");
            pubsubClient.subscribe(commandTopic);
            if(conf.debug) printDebug(F("Waiting for a message on topic: "));
            if(conf.debug) printlnDebug(commandTopic);
            MQTTConnectionSuccess();
        } else {
            if(conf.debug) printDebug(F("failed, rc="));
            if(conf.debug) printDebug(pubsubClient.state());
            if(conf.debug) printlnDebug(F(" try again in 5 seconds"));
            delay(5000);
        }
    }
    poweronMQTTLed();
    pubsubClient.loop();
}

void timedLoopSensorValues() {
    if (conf.autoReadTimer == 0) {
        return;
    }

    currentMillisAutoReadTimer = millis();
    if (currentMillisAutoReadTimer - lastTimeMillisAutoReadTimer >= conf.autoReadTimer) {
        if(conf.debug) printDebug(F("Sending timed sensor values after "));
        if(conf.debug) printlnDebug(currentMillisAutoReadTimer - lastTimeMillisAutoReadTimer);
        lastTimeMillisAutoReadTimer = millis();
        StaticJsonDocument<200> jsonDocument;
        for (int i = 0;i < conf.pins.size();i++) {
            pinConfiguration pin = conf.pins.get(i);
            pinStatus sensor = buildPinStatus(pin.id, pin);
            if (sensor.ioType == INPUT_TYPE) {
                if(conf.debug) printDebug(F("INPUT pin "));
                if(conf.debug) printlnDebug(sensor.pinId);
                jsonDocument[sensor.pinId] = readSensorValue(sensor);
            }
        }
        if(timedCommands.size()>0){
            jsonDocument[DELAY_TIME] = delayTime;
            getTimedCommands(jsonDocument);
        }
        String token = buildPeriodicToken();
        JsonObject jsonObject = jsonDocument.as<JsonObject>();
        publishMqttMessage(jsonObject, token);
    }
}

String buildPeriodicToken() {
    String token = "P";
    int actualValue = periodicTokenCounter;
    while (actualValue < 1000) {
        token += "0";
        actualValue *= 10;
    }
    token += periodicTokenCounter;
    periodicTokenCounter++;
    return token;
}

String buildTimestamp() {
    DateTime now = RTC.now();
    char buf[20];
    sprintf(buf, "%.4d%.2d%.2dT%.2d:%.2d:%.2d", now.year(), now.month(), now.day(), now.hour(), now.minute(), now.second());
    String timestamp = String(buf);
    return timestamp;
}

int receivedMessageCounter = 0;

void handleMQTTRequestCallback(char* topic, byte* payload, unsigned int length) {
    int pathIndex = 0;
    memset(&path,   0, sizeof(path));
    for (int i=0;i<length;i++) {
      //Serial.print((char)payload[i]);
      path[pathIndex++] = (char)payload[i];
    }
    //Serial.println();
    if(conf.debug) printDebug(F("Command: ")); if(conf.debug) printlnDebug(path);
    handleMQTTRequest(path);
    
    receivedMessageCounter++;
    Serial.print(F("Received messages:"));
    Serial.println(receivedMessageCounter);
    /*
    if ((receivedMessageCounter > 0) && (receivedMessageCounter%5 == 0)) {
      Serial.print(F("\nFree memory: "));
      Serial.println(freeMemory());
    }
    */
}

void handleMQTTRequest(char* commands) {
//    if(conf.debug) printlnDebug(freeMemory());
    LinkedList<String> queryStrings = splitCommands(commands);
    StaticJsonDocument<200> jsonDocument;
    for(int i = 0; i < queryStrings.size(); i++){
        String clientPath = queryStrings.get(i);
        if(clientPath.length()==0){
            continue;
        }
        String queryString = removeCharsBeforeString(clientPath, SEPARATOR);
        String password = extractPassword(queryString);
        String token = extractToken(queryString);
        if(!password.equals(conf.devicePassword)){
            jsonDocument["ERROR"] = "WRONG PASSWORD";
            JsonObject jsonObject = jsonDocument.as<JsonObject>();
            publishMqttMessage(jsonObject, token);
            return;
        }
        if (clientPath.startsWith("/write/")) {
            String outcome = handleOutput(queryString);
            if(outcome.equals(OK)){
                jsonDocument["RESULT"] = "SUCCESS";
                JsonObject jsonObject = jsonDocument.as<JsonObject>();
                publishMqttMessage(jsonObject, token);
            }else{
                jsonDocument["ERROR"] = outcome;
                JsonObject jsonObject = jsonDocument.as<JsonObject>();
                publishMqttMessage(jsonObject, token);
            }
        } else if (clientPath.startsWith("/timedWrite/")) {
            String outcome = handleTimedOutput(queryString);
            if(outcome.equals(OK)){
                jsonDocument["RESULT"] = "SUCCESS";
                JsonObject jsonObject = jsonDocument.as<JsonObject>();
                publishMqttMessage(jsonObject, token);
            }else{
                jsonDocument["ERROR"] = outcome;
                JsonObject jsonObject = jsonDocument.as<JsonObject>();
                publishMqttMessage(jsonObject, token);
            }
        } else if (clientPath.startsWith("/read/")) {
            JsonObject jsonObject = handleInput(queryString);
            publishMqttMessage(jsonObject, token);
        } else {
            jsonDocument["ERROR"] = "Unknown command " + clientPath;
            JsonObject jsonObject = jsonDocument.as<JsonObject>();
            publishMqttMessage(jsonObject, token);
        }
    }
}

LinkedList<String> splitCommands(char* commands){
    String commandString(commands);
    commandString.replace("[","");
    commandString.replace("]","");
    commandString.replace("\"","");
    return splitString(commandString, ",");
}

String extractToken(String string){
    int size = 6; // "token=".length()
    int position = string.indexOf("token=");
    if(position == -1){
        return "NO_TOKEN";
    }
    int passwordPosition = string.indexOf("password=");
    String token = string.substring(position + size, passwordPosition - 1);
    //if(conf.debug) Serial.println(token);
    return token;
}

String extractPassword(String string){
    int size = 9; // "password=".length()
    int position = string.indexOf("password=");
    if(position == -1){
        return "NO_PASSWORD";
    }
    String password = string.substring(position + size);
    password.trim();
    //if(conf.debug) Serial.println(password);
    return password;
}

void publishMqttMessage(JsonObject dataObject, String token){
    poweroffMQTTLed();
    StaticJsonDocument<200> jsonDocument;
    jsonDocument["data"] = dataObject;
    jsonDocument["token"] = token;
    String timestamp = buildTimestamp();
    jsonDocument["timestamp"] = timestamp;
    size_t bytes = serializeJson(jsonDocument, stringBuffer);
    if(conf.debug) printDebug(F("publishing message: "));
    if(conf.debug) printDebug(stringBuffer);
    if(conf.debug) printDebug(F(" of: "));
    if(conf.debug) printDebug(bytes);
    if(conf.debug) printDebug(F(" bytes"));
    if(conf.debug) printDebug(F(" on topic: "));
    if(conf.debug) printlnDebug(responseTopic);
    updateSignal();
    pubsubClient.publish(responseTopic, stringBuffer);
}

String removeCharsBeforeString(String originalString, char val){
    int position = originalString.indexOf(val);
    if (position == -1){
        return "";
    }
    return originalString.substring(position + 1);
}

////////////////////////////////////////////////////////////////
///////                    COMMON                    ////////////
////////////////////////////////////////////////////////////////

//////////////////      OUTPUT COMMAND    /////////////////////
String handleOutput(String command){
    //printPinConfiguration();
    LinkedList<String> outcomes = LinkedList<String>();
    LinkedList<queryStringCommand> commands = extractQueryStringCommands(command, outcomes);
    if(outcomes.size() > 0){
        return outcomes.get(0);
    }
    if (commands.size() == 0) {
        return "ERROR: Missing commands";
    }
    for(int i = 0; i < commands.size(); i++){
        queryStringCommand command = commands.get(i);
        String result = executeOutputCommand(command);
        if (!result.equals(OK)) {
            return result;
        }
    }
    commandSignal();
    return OK;
}

String executeOutputCommand(queryStringCommand command) {
    if(conf.debug) printDebug(F("Executing command for PIN number: "));
    if(conf.debug) printDebug(command.pinNumber);
    if(conf.debug) printDebug(F(" - Value: "));
    if(conf.debug) printlnDebug(command.value);
    if (command.ioType == INPUT_TYPE) {
        String error = "ERROR: Wrong IOType for pin number " + command.pinNumber;
        return error;
    }
    if (command.adType == DIGITAL_TYPE) {
        if (command.value == 0) {
            digitalWrite(command.pinNumber, LOW);
        } else if (command.value == 1) {
            digitalWrite(command.pinNumber, HIGH);
        }
    } else if (command.adType == ANALOG_TYPE) {
        analogWrite(command.pinNumber, command.value);
    }
    return OK;
}

LinkedList<queryStringCommand> extractQueryStringCommands(String command, LinkedList<String> outcomes) {
    LinkedList<queryStringCommand> result = LinkedList<queryStringCommand>();
    LinkedList<String> commandStrings = splitString(command, "&");
    for(int i = 0; i < commandStrings.size(); i++){
        String commandString = commandStrings.get(i);
        LinkedList<String> commandValues = splitString(commandString, "=");
        if (commandValues.size() != 2) {
            String error = "ERROR: Command with too many values";
            if(conf.debug) Serial.println(error);
            outcomes.add(error);
            result.clear();
            return result;
        }
        String pinId = commandValues.get(0);
        String valueString = commandValues.get(1);
        char buf[32];
        stringToCharArray(valueString, buf);
        long value = atol(buf); 
        if(pinId.equals("token") || pinId.equals("password")){
            continue;
        }
        if (pinId.equals("timer")) {
            queryStringCommand timerCommand;
            timerCommand.pinId = "timer";
            timerCommand.value = value;
            if (value < 0) {
                String error = "ERROR: Negative timer " + value;
                if(conf.debug) Serial.println(error);
                outcomes.add(error);
                result.clear();
                return result;
            }
            result.add(timerCommand);
            continue;
        }
        int pinIndex = findPinIndex(pinId);
        if (pinIndex == -1) {
            String error = "ERROR: Pin not existing " + pinId;
            if(conf.debug) Serial.println(error);
            outcomes.add(error);
            result.clear();
            return result;
        }
        pinConfiguration pin = conf.pins.get(pinIndex);
        queryStringCommand command;
        command.pinId = pin.id;
        command.pinNumber = pin.pinNumber;
        command.adType = pin.adType;
        command.ioType = pin.ioType;
        command.sensorType = pin.sensorType;
        command.value = value;
        result.add(command);
    }
    return result;
}

LinkedList<String> splitString(String string, String separator){
    LinkedList<String> result = LinkedList<String>();
    int position = string.indexOf(separator);
    while(position > 0){
        String token = string.substring(0, position);
        result.add(token);
        string = string.substring(position + 1);
        position = string.indexOf(separator);
    }
    result.add(string);
    return result;
}

int findPinIndex(String pinId) {
    for (int i = 0; i < conf.pins.size(); i++) {
        if (conf.pins.get(i).id.equals(pinId)) {
            return i;
        }
    }
    return -1;
}

//////////////////      TIMED OUTPUT COMMAND    /////////////////////
String handleTimedOutput(String queryString){
    //printPinConfiguration();
    LinkedList<String> outcomes = LinkedList<String>();
    if(conf.debug) printDebug(F("Timing commands: "));    
    LinkedList<queryStringCommand> commands = extractQueryStringCommands(queryString, outcomes);
    if(outcomes.size() > 0){
        return outcomes.get(0);
    }
    if (commands.size() == 0) {
        if(conf.debug) printDebug(F("No commands to time, returning..."));    
        return "Missing commands";
    }
    queryStringCommand timerCommand = commands.get(0);
    String timerString = timerCommand.pinId;
    if (!timerString.equals("timer")) {
        if(conf.debug) printDebug(F("First command is not 'timer', returning..."));    
        return "Incorrect syntax. Command must start with timedWrite";
    }
    noInterrupts();
    if(timerActive){
        stopTimer();
    }
    timedCommands = LinkedList<queryStringCommand>();
    for(int i = 1; i < commands.size(); i++){
        timedCommands.add(commands.get(i));
    }
    if(timerCommand.value > 0){
        startTimer(timerCommand.value);
    }
    interrupts();
    return OK;
}

void executeTimedOutputCommands() { 
    //enable interrupts so serial can work
    if(conf.debug) sei();
    if(!timerActive){
        //if(conf.debug) Serial.println("No active timer...");
        return;
    }
    if(delayTime > 0){
        delayTime = delayTime - 1;
        if(conf.debug) Serial.print("Seconds for next command ");
        if(conf.debug) Serial.println(delayTime);
        return;
    }
    if(conf.debug) Serial.println("Executing timed commands...");
    for (int i = 0; i < timedCommands.size(); i++) {
        executeOutputCommand(timedCommands.get(i));
    }
    stopTimer();
}

//////////////////      INPUT COMMAND    /////////////////////
JsonObject handleInput(String queryString){
    LinkedList<String> outcomes = LinkedList<String>();
    LinkedList<pinStatus> sensors = extractQueryStringSensors(queryString, outcomes);
    StaticJsonDocument<200> jsonDocument;
    if (outcomes.size() > 0) {
        jsonDocument["Message"] = outcomes.get(0);
        JsonObject jsonObject = jsonDocument.as<JsonObject>();
        return jsonObject;
    }
    for(int i=0; i < sensors.size(); i++){
        pinStatus sensor = sensors.get(i);
        jsonDocument[sensor.pinId] = readSensorValue(sensor);
    }
    if(timedCommands.size()>0){
        jsonDocument[DELAY_TIME] = delayTime;
        getTimedCommands(jsonDocument);
    }
    JsonObject jsonObject = jsonDocument.as<JsonObject>();
    return jsonObject;
}

LinkedList<pinStatus> extractQueryStringSensors(String queryString, LinkedList<String> outcomes) {
    LinkedList<pinStatus> sensors = LinkedList<pinStatus>();
    LinkedList<String> sensorStrings = splitString(queryString, "&");
    for(int i = 0; i < sensorStrings.size(); i++){
        String sensorString = sensorStrings.get(i);
        if(sensorString.startsWith("token") || sensorString.startsWith("password")){
            continue;
        }
        int pinIndex = findPinIndex(sensorString);
        if (pinIndex == -1) {
            String message = "ERROR: Pin " + sensorString + "not existing";
            if(conf.debug) Serial.println(message);
            outcomes.add(message);
            sensors.clear();
            return sensors;
        }
        pinConfiguration pin = conf.pins.get(pinIndex);
        pinStatus sensor = buildPinStatus(sensorString, pin);
        sensors.add(sensor);
    }
    return sensors;
}

pinStatus buildPinStatus(String sensorString, pinConfiguration pin) {
    pinStatus sensor;
    sensor.pinId = sensorString;
    sensor.pinNumber = pin.pinNumber;
    sensor.adType = pin.adType;
    sensor.ioType = pin.ioType;
    sensor.sensorType = pin.sensorType;
    return sensor;
}

int readSensorValue(pinStatus sensor) {
    int value;
    if (sensor.sensorType == SENSOR_TYPE_RAW) {
        value = readRawSensorValue(sensor);
    } else if (sensor.sensorType == SENSOR_TYPE_ULTRASOUND) {
        value = readUltrasoundSensorValue(sensor);
    }
    return value;
}

int readRawSensorValue(pinStatus sensor) {
    int rawValue;
    if (sensor.adType == DIGITAL_TYPE) {
        rawValue = digitalRead(sensor.pinNumber);
    } else if (sensor.adType == ANALOG_TYPE) {
        rawValue = analogRead(sensor.pinNumber);
    } else if (sensor.adType == ANALOG_AS_DIGITAL) {
        int tmpValue = analogRead(sensor.pinNumber);
        if(conf.debug) {
            printDebug(F("Sensor: "));printDebug(sensor.pinId);printDebug(F(" - RAW Value = "));printlnDebug(tmpValue);
        }
        if (tmpValue >= ANALOG_AS_DIGITAL_TSD) {
            rawValue = 1;
        } else {
            rawValue = 0;
        }
    }
    return rawValue;
}

int readUltrasoundSensorValue(pinStatus sensor) {
    if (sensor.adType != DIGITAL_TYPE) {
        errorSignal();
        return -1;
    }
    digitalWrite(TRIGGER_PIN, LOW);
    delayMicroseconds(2);
    digitalWrite(TRIGGER_PIN, HIGH);
    delayMicroseconds(10);
    digitalWrite(TRIGGER_PIN, LOW);
    int duration = pulseIn(sensor.pinNumber, HIGH);
    int distance = duration / 58; // per i pollici la formula è durata / 148;
    //distance = duration * 0.034 / 2; // Speed of sound wave divided by 2 (go and back)

    if (sensor.pinId.equals("s1")) {
        //if(conf.debug) {
            printDebug(F("Sensor: "));printDebug(sensor.pinId);printDebug(F(" - RAW Value = "));printlnDebug(distance);
        //}
        if ((distance >= 5) && (distance <= 15)) {
            distance = 1;
        } else {
            distance = 0;
        }
    } else if (sensor.pinId.equals("s2")) {
        //if(conf.debug) {
            printDebug(F("Sensor: "));printDebug(sensor.pinId);printDebug(F(" - RAW Value = "));printlnDebug(distance);
        //}
        if ((distance >= 2) && (distance <= 10)) {
            distance = 1;
        } else {
            distance = 0;
        }
    } else {
        distance = -1;
    }
    return distance;
}

void getTimedCommands(JsonDocument& jsonDocument){
    for(int i = 0; i < timedCommands.size(); i++){
        queryStringCommand command = timedCommands.get(i);
        jsonDocument[TIMED_PIN_PREFIX + command.pinId] = command.value;
    }
}

////////////////////////////////////////////////////////////////
///////             CONFIGURATION                   ////////////
////////////////////////////////////////////////////////////////
void loadConfiguration() {
    loadConfigurationPlain();
    //checkConfiguration();
    //loadConfigurationMock();
}

#ifdef SDCS
    #define SD_CS_PIN SDCS
#else
    #define SD_CS_PIN SS
#endif

void loadConfigurationPlain() {
    Serial.println(F("SD initialization..."));
    SdFat SD;
    if (!SD.begin(SD_CS_PIN, SD_SCK_MHZ(15))) {
        Serial.println(F("SD initialization failed!"));
        systemStatus = SYSTEM_STATUS_KO;
        errorSignal();
        return;
    }
    Serial.println("Initialization done.");
    File myFile = SD.open(CONF_FILE);
    if (!myFile) {
        Serial.print(F("error opening "));
        Serial.println(CONF_FILE);
        systemStatus = SYSTEM_STATUS_KO;
        errorSignal();
        return;
    }
    //Serial.print(F("Reading from "));
    //Serial.println(CONF_FILE);
    String configString;
    char data;
    boolean pinSection = false;
    int loadedPinSettings = 0;
    pinConfiguration pin;
    conf.pins = LinkedList<pinConfiguration>();
    while ((data = myFile.read()) >= 0) {
        configString = configString + data;
        if ((configString.indexOf("\n") > 0) && configString.startsWith("//")) {
            configString = "";
            continue;
        }
        if ((configString.indexOf("\n") > 0) && !pinSection) {
            pinSection = loadConfigurationParameters(configString);
            configString = "";
        } else if ((configString.indexOf("\n") > 0) && pinSection) {
            loadedPinSettings++;
            loadConfigurationPins(configString, pin);
            if (loadedPinSettings == 5) {
                conf.pins.add(pin);
                loadedPinSettings = 0;
            }
            configString = "";
        }
    }
    myFile.close();
    //TODO: Read configuration
    conf.mac[0] = 0xDE;
    conf.mac[1] = 0xAD;
    conf.mac[2] = 0xBE;
    conf.mac[3] = 0xFE;
    conf.mac[4] = 0xEF;
    conf.mac[5] = 0xED;
}

int loadConfigurationParameters(String row) {
    if (row.startsWith("debug")) {
        String debugValue = extractValues(row);
        conf.debug = debugValue.toInt();
        if(conf.debug == ON) {
            Serial.println(F("DEBUG MODE = ON"));
        } else {
            Serial.println(F("DEBUG MODE = OFF"));
        }
        //conf.debug = 1;
        return false;
    }
    if (row.startsWith("sound")) {
        String soundValue = extractValues(row);
        conf.sound = soundValue.toInt();
        if(conf.sound == ON) {
            Serial.println(F("SOUND = ON"));
        } else {
            Serial.println(F("SOUND = OFF"));
        }
        return false;
    }
    if (row.startsWith("messageBeep")) {
        String messageBeepValue = extractValues(row);
        messageBeep = messageBeepValue.toInt();
        if(messageBeep == ON) {
            Serial.println(F("MESSAGE BEEP = ON"));
        } else {
            Serial.println(F("MESSAGE BEEP = OFF"));
        }
        return false;
    }
    if (row.startsWith("ssid")) {
        conf.ssid = extractValues(row);
        //conf.ssid = "moto g(7) power 9667";
        return false;
    }
    if (row.startsWith("protectionType")) {
        String protectionTypeValue = extractValues(row);
        conf.protectionType = protectionTypeValue.toInt();
        return false;
    }
    if (row.startsWith("wifiPassword")) {
        conf.wifiPassword = extractValues(row);
        //conf.wifiPassword = "soffitta.31";
        return false;
    }
    if (row.startsWith("devicePassword")) {
        conf.devicePassword = extractValues(row);
        return false;
    }
    if (row.startsWith("ipStrategy")) {
        String ipStrategyValue = extractValues(row);
        conf.ipStrategy = ipStrategyValue.toInt();
        return false;
    }
    if (row.startsWith("ip")) {
        String ipValue = extractValues(row);
        parseIp(ipValue, conf.ip);
        return false;
    }
    if (row.startsWith("gateway")) {
        String gatewayValue = extractValues(row);
        parseIp(gatewayValue, conf.gateway);
        return false;
    }
    if (row.startsWith("subnet")) {
        String subnetValue = extractValues(row);
        parseIp(subnetValue, conf.subnet);
        return false;
    }
    if (row.startsWith("dns")) {
        String dnsValue = extractValues(row);
        parseIp(dnsValue, conf.dns);
        return false;
    }
    if (row.startsWith("deviceId")) {
        conf.id = extractValues(row);
        return false;
    }
    //if (row.startsWith("pubkey")) {
    //    stringToCharArray(extractValues(row), conf.pubkey);
    //    return false;
    //}
    //if (row.startsWith("subkey")) {
    //    stringToCharArray(extractValues(row), conf.subkey);
    //    return false;
    //}
    if (row.startsWith("mqttServer")) {
        String mqttServerValue = extractValues(row);
        //mqttServerValue = "192.168.43.18";
        parseIp(mqttServerValue, conf.mqttServer);
        return false;
    }
    if (row.startsWith("mqttPort")) {
        String portValue = extractValues(row);
        conf.mqttPort = portValue.toInt();
        return false;
    }
    if (row.startsWith("autoReadTimer")) {
        String autoReadTimerValue = extractValues(row);
        conf.autoReadTimer = autoReadTimerValue.toInt();
        return false;
    }
    if (row.startsWith("--pins--")) {
        return true;
    }
    return false;
}

void loadConfigurationPins(String row, struct pinConfiguration &pin) {
    if (row.startsWith("pinNumber")) {
        String pinNumberValue = extractValues(row);
        pin.pinNumber = pinNumberValue.toInt();
    }
    if (row.startsWith("ioType")) {
        String ioTypeValue = extractValues(row);
        pin.ioType = ioTypeValue.toInt();
    }
    if (row.startsWith("adType")) {
        String adTypeValue = extractValues(row);
        pin.adType = adTypeValue.toInt();
    }
    if (row.startsWith("sensorType")) {
        String sensorTypeValue = extractValues(row);
        pin.sensorType = sensorTypeValue.toInt();
    }
    if (row.startsWith("id")) {
        String idValue = extractValues(row);
        pin.id = idValue;
    }
}

void checkConfiguration() {
    if (conf.id.equals("")) {
        errorSignal();
        while(TRUE);
    }
    if (conf.ssid.equals("")) {
        errorSignal();
        while(TRUE);
    }
    /*if ((conf.protectionType != 1) && (conf.protectionType != 2)) {
        errorSignal();
        while(TRUE);
    }*/
    if (conf.wifiPassword.equals("")) {
        errorSignal();
        while(TRUE);
    }
    //if (conf.devicePassword.equals("") && conf.local != 1) {
    if (conf.devicePassword.equals("")) {
        errorSignal();
        while(TRUE);
    }
    if ((conf.ipStrategy != 1) || (conf.ipStrategy != 2)) {
        errorSignal();
        while(TRUE);
    }
    //if (!conf.mqttServer.isSet()) {
    //    errorSignal();
    //    while(TRUE);
    //}
    if (conf.mqttPort < 1) {
        errorSignal();
        while(TRUE);
    }
    if (conf.autoReadTimer < 0) {
        errorSignal();
        while(TRUE);
    }
    for (int i = 0; i < conf.pins.size();i++) {
        if ((conf.pins.get(i).pinNumber < 0) || (conf.pins.get(i).pinNumber > 53)) {
            errorSignal();
            while(TRUE);
        }
        if ((conf.pins.get(i).ioType != 0) && (conf.pins.get(i).ioType != 1)) {
            errorSignal();
            while(TRUE);
        }
        if ((conf.pins.get(i).adType != 0) && (conf.pins.get(i).adType != 1)) {
            errorSignal();
            while(TRUE);
        }
        if (conf.pins.get(i).id.equals("")) {
            errorSignal();
            while(TRUE);
        }
    }
}

String extractValues(String row) {
    String configValue = row.substring(row.indexOf(":") + 1);
    configValue.trim();
    return configValue;
}

void parseIp(String value, byte ip[4]) {
    for(int i = 0;i < 4;i++) {
        String s = value.substring(0,value.indexOf("."));
        value = value.substring(value.indexOf(".") + 1);
        value.trim();
        ip[i] = s.toInt();
    }
}

void parseIp(String value, IPAddress &ipAddress) {
    ipAddress.fromString(value);
}

void printPinConfiguration() {
    if(conf.debug) printlnDebug(F("\n******** CONFIG **********"));
    if(conf.debug) printlnDebug(conf.id);
    for (int i = 0; i < conf.pins.size(); i++) {
        pinConfiguration pin = conf.pins.get(i);
        printPin(pin);
    }
}

void printPin(pinConfiguration pin) {
    if(conf.debug) printDebug(F("id: "));
    if(conf.debug) printDebug(pin.id);    
    if(conf.debug) printDebug(F(" - pinNumber: "));
    if(conf.debug) printDebug(pin.pinNumber);
    if(conf.debug) printDebug(F(" - ioType: "));
    if(conf.debug) printDebug(pin.ioType);
    if(conf.debug) printDebug(F(" - adType: "));
    if(conf.debug) printDebug(pin.adType);
    if(conf.debug) printDebug(F(" - sensorType: "));
    if(conf.debug) printlnDebug(pin.sensorType);
}

////////////////////////////////////////////////////////////////
///////                    DEBUG                    ////////////
////////////////////////////////////////////////////////////////

void poweronSignal() {
    if (conf.sound == ON) {
        tone(SPEAKER_PIN, 440, 500);
    }
}

void commandSignal() {
    if((conf.sound == ON) && (messageBeep == ON)) {
        tone(SPEAKER_PIN, 440, 50);
    }
}

void updateSignal() {
    if((conf.sound == ON) && (messageBeep == ON)) {
        tone(SPEAKER_PIN, 1200, 100);
    }
}

void connectionSignal() {
    if (conf.sound == ON) {
        //tone(SPEAKER_PIN, 440, 200);
        for(uint8_t nLoop = 0;nLoop < 2;nLoop ++) {
           tone(SPEAKER_PIN,NOTE_A5);
           delay(NOTE_SUSTAIN);
           tone(SPEAKER_PIN,NOTE_B5);
           delay(NOTE_SUSTAIN);
           tone(SPEAKER_PIN,NOTE_C5);
           delay(NOTE_SUSTAIN);
           tone(SPEAKER_PIN,NOTE_B5);
           delay(NOTE_SUSTAIN);
           tone(SPEAKER_PIN,NOTE_C5);
           delay(NOTE_SUSTAIN);
           tone(SPEAKER_PIN,NOTE_D5);
           delay(NOTE_SUSTAIN);
           tone(SPEAKER_PIN,NOTE_C5);
           delay(NOTE_SUSTAIN);
           tone(SPEAKER_PIN,NOTE_D5);
           delay(NOTE_SUSTAIN);
           tone(SPEAKER_PIN,NOTE_E5);
           delay(NOTE_SUSTAIN);
           tone(SPEAKER_PIN,NOTE_D5);
           delay(NOTE_SUSTAIN);
           tone(SPEAKER_PIN,NOTE_E5);
           delay(NOTE_SUSTAIN);
           tone(SPEAKER_PIN,NOTE_E5);
           delay(NOTE_SUSTAIN);
        }
        noTone(SPEAKER_PIN);
    }
}

void errorSignal() {
    if (conf.sound == ON) {
        tone(SPEAKER_PIN,NOTE_G4);
        delay(250);
        tone(SPEAKER_PIN,NOTE_C4);
        delay(500);
        noTone(SPEAKER_PIN);
    }
}

void systemStatusError() {
    currentMillisSystemStatusLed = millis();
    if ((currentMillisSystemStatusLed - lastTimeMillisSystemStatusLed >= SYSTEMSTATUS_BLINK_TIME) &&
        (statusLed == OFF)) {
        lastTimeMillisSystemStatusLed = millis();
        statusLed = ON;
        digitalWrite(PIN_STATUS_LED, HIGH);
    }

    if ((currentMillisSystemStatusLed - lastTimeMillisSystemStatusLed >= SYSTEMSTATUS_BLINK_TIME) &&
        (statusLed == ON)) {
        lastTimeMillisSystemStatusLed = millis();
        statusLed = OFF;
        digitalWrite(PIN_STATUS_LED, LOW);
    }
}

void wifiConnectionSuccess() {
    digitalWrite(PIN_STATUS_OK_LED, HIGH);
    digitalWrite(PIN_STATUS_LED, LOW);
    digitalWrite(PIN_STATUS_KO_LED, LOW);
    connectionSignal();
}

void wifiConnectionError() {
    digitalWrite(PIN_STATUS_OK_LED, LOW);
    digitalWrite(PIN_STATUS_LED, LOW);
    digitalWrite(PIN_STATUS_KO_LED, HIGH);
    errorSignal();
}

void MQTTConnectionSuccess() {
    digitalWrite(PIN_MQTT_LED, HIGH);
    tone(SPEAKER_PIN, 1200, 100);
}

void poweronMQTTLed() {
    currentMillisMQTTLed = millis();
    if (currentMillisMQTTLed - lastTimeMillisMQTTLed >= MQTT_BLINK_TIME) {
        digitalWrite(PIN_MQTT_LED, HIGH);
    }
}

void poweroffMQTTLed() {
    if (currentMillisMQTTLed - lastTimeMillisMQTTLed >= MQTT_BLINK_TIME) {
        lastTimeMillisMQTTLed = millis();
        digitalWrite(PIN_MQTT_LED, LOW);
    }
}

////////////////////////////////////////////////////////////////
///////                    LOG                     ////////////
////////////////////////////////////////////////////////////////
void printDebug(String s){
    Serial.print(s);
}

void printlnDebug(String s){
    Serial.println(s);
}

void printDebug(long i){
    Serial.print(i);
}

void printlnDebug(long i){
    Serial.println(i);
}
