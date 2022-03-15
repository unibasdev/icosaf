package it.unibas.arduino.config.vista;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextFieldLimit extends PlainDocument {
    
    private int limit;
    JTextField textField;

    public TextFieldLimit(int limit, JTextField textField) {
        super();
        this.limit = limit;
        this.textField = textField;
    }

    public TextFieldLimit(int limit, boolean upper) {
        super();
        this.limit = limit;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
//        try {
//            int parseInt = Integer.parseInt(str);
//        } catch (Exception e) {
//            return;
//        }

        if (str == null || str.length() > limit) {
            return;
        }

        if (getLength() > 2) {
            logger.debug(" - replace -" + " - getLength -" + getLength() + "offset: " + offset);
//                logger.debug("str.length: " + str.length());
            super.replace(offset, str.length(), str, attr);
        }

        if ((getLength() + str.length()) <= limit) {
            logger.debug(" getLength : " + getLength() + " str : " + str.length());
            super.insertString(offset, str, attr);
        }
//            else {
        if (offset >= 2) {
            logger.debug(" next focus");
            this.textField.transferFocus();
//                JTextField nextFocusableComponent = (JTextField) this.textField.getNextFocusableComponent();
//                if (nextFocusableComponent != null) {
//                    nextFocusableComponent.setCaretPosition(0);
//                }
        }
//            }
    }

    @Override
    public synchronized Position createPosition(int offs) throws BadLocationException {
        if (offs == 2) {
            offs = 0;
        }
        return super.createPosition(offs);
    }

}
