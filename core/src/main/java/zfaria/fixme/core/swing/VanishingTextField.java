package zfaria.fixme.core.swing;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class VanishingTextField extends JTextField {

    private String emptyText;

    public VanishingTextField(String text) {
        super(text);
        emptyText = text;
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                if (getText().equals(emptyText)) {
                    setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                if (getText().equals("")) {
                    setText(emptyText);
                }
            }
        });
    }
}
