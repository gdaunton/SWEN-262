package main.view.elements;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.StringUtils;

public class IntegerTextField extends TextField {
    public IntegerTextField() {
        super();

        addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (!isValid(getText())) {
                    event.consume();
                }
            }
        });

        textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observableValue,
                                String oldValue, String newValue) {
                if(!isValid(newValue)) {
                    setText(oldValue);
                }
            }
        });
    }

    private boolean isValid(final String value) {
        if (value.length() == 0) {
            return true;
        }
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public int getInteger() {
        try {
            return Integer.parseInt(getText());
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }
}
