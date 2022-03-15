package it.unibas.arduino.client;

import it.unibas.arduino.client.model.ClientConfiguration;
import it.unibas.arduino.client.model.Command;
import it.unibas.arduino.client.model.PinValue;
import it.unibas.arduino.client.model.operator.ClientFactory;
import it.unibas.arduino.client.model.operator.ClientMQTT;
import it.unibas.arduino.client.model.operator.CommandExecutionException;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import it.unibas.arduino.client.model.operator.IClient;
import org.junit.Assert;
import org.junit.Ignore;

@Slf4j
@Ignore
public class TestMQTT {

    private ClientConfiguration mqttConfiguration = new ClientConfiguration("mqtt-icosaf", "xxx.xxx.xxx.xxx", 1883, "((icosaf))");
    private IClient client;

    @Before
    public void setUp() {
        try {
            client = ClientFactory.getInstance().getClient(mqttConfiguration);
            assertEquals(true, client instanceof ClientMQTT);
        } catch (CommandExecutionException ex) {
            logger.error("Unable to load client", ex);
            fail(ex.getLocalizedMessage());
        }
        turnOffLeds();
    }

    @After
    public void tearDown() {
        if (client != null) client.close();
    }

    @Test
    public void testRead() {
        logger.debug("Sending data");
        try {
            Command commandWrite = new Command(Constants.WRITE);
            commandWrite.addPinValue(new PinValue("led_0", 1));
            commandWrite.addPinValue(new PinValue("led_1", 0));
            commandWrite.addPinValue(new PinValue("led_2", 1));
            commandWrite.addPinValue(new PinValue("led_3", 0));
            commandWrite.addPinValue(new PinValue("led_4", 1));
            commandWrite.addPinValue(new PinValue("led_5", 0));
            client.execute(commandWrite);
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
            client.execute(commandRead);
            assertEquals(1, (int) pin0.getValue());
            assertEquals(0, (int) pin1.getValue());
            assertEquals(1, (int) pin2.getValue());
            assertEquals(0, (int) pin3.getValue());
            assertEquals(1, (int) pin4.getValue());
            assertEquals(5, commandRead.getPinValues().size());
        } catch (Exception ex) {
            logger.error("Unable to execute command", ex);
//            Assert.fail(ex.getLocalizedMessage());
        }
    }

    private void turnOffLeds() {
        try {
            Command command = new Command(Constants.WRITE);
            command.addPinValue(new PinValue("led_0", 0));
            command.addPinValue(new PinValue("led_1", 0));
            command.addPinValue(new PinValue("led_2", 0));
            command.addPinValue(new PinValue("led_3", 0));
            command.addPinValue(new PinValue("led_4", 0));
            command.addPinValue(new PinValue("led_5", 0));
            client.execute(command);
        } catch (CommandExecutionException ex) {
            ex.printStackTrace();
            Assert.fail(ex.getLocalizedMessage());
        }
    }

}
