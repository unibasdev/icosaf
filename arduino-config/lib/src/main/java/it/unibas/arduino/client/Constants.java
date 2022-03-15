package it.unibas.arduino.client;

public class Constants {

    public static final String LOCAL_COMMAND_SEPARATOR = "-";
    public static final String INTERNET_COMMAND_SEPARATOR = "-";
    public static final String PIN_SEPARATOR = "&";

    public static final String READ = "read";
    public static final String WRITE = "write";
//    public static final String TIMED_READ = "timedRead";
    public static final String TIMED_WRITE = "timedWrite";
    public static final String[] COMMAND_TYPES = new String[]{READ, WRITE, TIMED_WRITE};
//    public static final String[] COMMAND_TYPES = new String[]{READ, WRITE, TIMED_READ, TIMED_WRITE};

    public static final String TIMED_PIN_PREFIX = "timed_";
    public static final String TIMER_PIN = "timer";

    public static final String SUCCESS = "SUCCESS";
    public static final String TIMED = "TIMED";
    public static final String ERROR_PREFIX = "ERROR:";

    public static final String DELAY_TIME = "delayTime";
    public static final String TOKEN = "token";
    public static final String PASSWORD = "password";
    public static final int TOKEN_BOUND = 10000;

    //
    public static final String PUBNUB_PUB_KEY = "pub-c-3e455616-7b9e-4f7b-8dac-a54bb31f016c";
    public static final String PUBNUB_SUB_KEY = "sub-c-24c26038-cf1b-11e4-85c8-02ee2ddab7fe";
    public static final String COMMAND_CHANNEL = "command_data";
    public static final String RESPONSE_CHANNEL = "sensors_data";

    public static final int TIMEOUT_ITERATIONS = 2;
    public static final int CHECK_INTERVAL = 500;
    public static final int TOTAL_TIMEOUT = TIMEOUT_ITERATIONS * CHECK_INTERVAL;
}
