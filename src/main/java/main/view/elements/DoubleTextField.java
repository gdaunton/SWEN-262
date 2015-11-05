package main.view.elements;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.StringUtils;

public class DoubleTextField extends TextField {

    private OnEnterPressed enter;

    /**
     * Sets up a double text field.
     */
    public DoubleTextField() {
        super();

        textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!isValid(newValue))
                setText(oldValue);
        });

        addEventFilter(KeyEvent.KEY_TYPED, event -> {
            char[] temp = event.getCharacter().toCharArray();
            if (!isValid(getText()))
                event.consume();
            else if (temp.length > 0 && temp[0] == 13 && enter != null)
                enter.handle();
        });
    }

    /**
     * Set the enter pressed listener
     *
     * @param listener the listener
     */
    public void setOnEnterPressedListener(OnEnterPressed listener) {
        enter = listener;
    }

    /**
     * The value of the field
     *
     * @param value the value
     */
    public void setValue(double value) {
        setText(Double.toString(value));
    }

    /**
     * Checks validity of entered value.
     *
     * @param value The value.
     * @return True if valid; false otherwise.
     */
    private boolean isValid(final String value) {
        if (value.length() == 0) {
            return true;
        }

        if (StringUtils.countMatches(value, ".") > 1) {
            return false;
        }
        if (value.endsWith(".")) {
            return true;
        }

        try {
            Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    /**
     * Gets a double from the entry.
     *
     * @return The double.
     */
    public double getDouble() {
        try {
            return Double.parseDouble(getText());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public interface OnEnterPressed {
        void handle();
    }
}
