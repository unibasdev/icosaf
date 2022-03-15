package it.unibas.arduino.client.model.operator;

import it.unibas.arduino.client.model.Command;

public interface IClient {

    public void execute(Command command) throws CommandExecutionException;

    public void close();
}
