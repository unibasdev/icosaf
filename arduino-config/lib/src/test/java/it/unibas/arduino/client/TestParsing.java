package it.unibas.arduino.client;

import it.unibas.arduino.client.model.Command;
import it.unibas.arduino.client.model.PinValue;
import it.unibas.arduino.client.model.operator.CommandExecutionException;
import it.unibas.arduino.client.model.operator.ParseResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class TestParsing {

    private ParseResponse parser = new ParseResponse();

    @Test
    public void parseReadResponse() {
        Command commandRead = new Command(Constants.READ);
        PinValue pin0 = new PinValue("s1");
        commandRead.addPinValue(pin0);
        PinValue pin1 = new PinValue("s2");
        commandRead.addPinValue(pin1);
        PinValue pin2 = new PinValue("s3");
        commandRead.addPinValue(pin2);
        PinValue pin3 = new PinValue("s4");
        commandRead.addPinValue(pin3);
        PinValue pin4 = new PinValue("s5");
        commandRead.addPinValue(pin4);
        String message = "{\"data\":{\"s1\":0,\"s2\":0,\"s3\":0,\"s4\":1,\"s5\":0},\"token\":\"T6737\",\"timestamp\":\"20220128T16:59:22\"}";
        try {
            parser.parse(message, commandRead);
        } catch (Exception ex) {
            logger.error("Unable to parse message {}, {}", message, ex.getMessage(), ex);
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void parseSuccessWriteResponse() {
        Command commandWrite = new Command(Constants.WRITE);
        commandWrite.addPinValue(new PinValue("led_0", 1));
        String message = "{\"RESULT\":\"SUCCESS\",\"token\":\"T3420\",\"timestamp\":\"20210622T19:57:35\"}";
        try {
            parser.parse(message, commandWrite);
        } catch (Exception ex) {
            logger.error("Unable to parse message {}, {}", message, ex.getMessage(), ex);
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void parseErrorWriteResponse() {
        Command commandWrite = new Command(Constants.WRITE);
        commandWrite.addPinValue(new PinValue("led_0", 1));
        String message = "{\"ERROR\":\"Unknown command xxx\",\"token\":\"T3420\",\"timestamp\":\"20210622T19:57:35\"}";
        try {
            parser.parse(message, commandWrite);
            Assert.fail();
        } catch (CommandExecutionException ex) {
        } catch (Exception ex) {
            logger.error("Unable to parse message {}, {}", message, ex.getMessage(), ex);
            Assert.fail(ex.getMessage());
        }
    }

}
