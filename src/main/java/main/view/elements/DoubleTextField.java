package main.view.elements;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class DoubleTextField extends TextField {

	/**
	 * Sets up a double text field.
	 */
	public DoubleTextField() {
		super();

		addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (!isValid(getText())) {
					event.consume();
				}
			}
		});

		textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				if (!isValid(newValue)) {
					setText(oldValue);
				}
			}
		});
	}

	/**
	 * Checks validity of entered value.
	 * 
	 * @param value
	 *            The value.
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
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
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
			return 0;
		}
	}
}
