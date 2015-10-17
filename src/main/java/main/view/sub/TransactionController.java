package main.view.sub;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import main.controller.command.HoldingCommand;
import main.model.Record;
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
	@FXML
	private TextField type;

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
	public void setTransaction(MainController controller, Record record) {
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
		type.setDisable(disable);
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
			
			type.setText(record.type.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
