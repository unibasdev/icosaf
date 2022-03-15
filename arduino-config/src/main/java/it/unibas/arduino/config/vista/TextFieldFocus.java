package it.unibas.arduino.config.vista;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;

public class TextFieldFocus extends FocusAdapter {

    @Override
    public void focusGained(FocusEvent e) {
        ((JTextField) e.getComponent()).setCaretPosition(0);
    }

}
