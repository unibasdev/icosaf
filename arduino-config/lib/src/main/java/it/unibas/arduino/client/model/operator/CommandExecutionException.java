package it.unibas.arduino.client.model.operator;

public class CommandExecutionException extends Exception {

    public CommandExecutionException() {
    }

    public CommandExecutionException(String msg) {
        super(msg);
    }

    public CommandExecutionException(Exception e) {
        super(e);
    }
}
