package main.view.sub;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import main.controller.command.HoldingCommand;
import main.model.holdings.Record;
import main.view.MainController;


public class TransactionController {

	@FXML
	private TextField h1;
	@FXML
	private TextField h2;
	@FXML
	private TextField amount;
	@FXML
	private TextField date;

	private Record record;
	private MainController controller;

	/**
	 * Sets the record.
	 * 
	 * @param controller
	 *            The controller.
	 * @param record
	 *            The record.
	 */
	public void setAccount(MainController controller, Record record) {
		this.record = record;
		this.controller = controller;
		initValues();
		disableElements(true);
	}

	/**
	 * Initializes the record controller.
	 * 
	 * @param location
	 *            The location.
	 * @param resources
	 *            The resources.
	 */
	public void initialize(URL location, ResourceBundle resources) {}

	/**
	 * Sets the disabled elements.
	 * 
	 * @param disabled
	 *            True to disable; false otherwise.
	 */
	private void disableElements(boolean disabled) {
		h1.setDisable(disabled);
		h2.setDisable(disabled);
		amount.setDisable(disabled);
		date.setDisable(disabled);
	}

	/**
	 * Sets the initial values.
	 */
	private void initValues() {
		try {
			h1.setText(record.h1ToString());
			h2.setText(record.h2ToString());
			amount.setText(NumberFormat.getCurrencyInstance().format(record.amount));
			
			SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
			date.setText(sdf.format(record.date));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
