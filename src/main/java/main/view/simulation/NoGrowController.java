package main.view.simulation;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.controller.command.HoldingCommand;
import main.model.Portfolio;
import main.model.simulation.NoGrowth;
import main.view.MainController;


public class NoGrowController {

	//buttons
	@FXML
	private Button run;
	@FXML
	private Button restart;
	
	//configuration
		//dropdown for choosing the simulation length
	
	//rate of growth? would be a textfield
	
	//portfolio summation stuff
	

	//objects
	private Portfolio p;
	private Portfolio op;
	private MainController controller;
	private NoGrowth s = new NoGrowth();

	/**
	 * Sets the record.
	 * 
	 * @param controller
	 *            The controller.
	 * @param p
	 *            The portfolio.
	 */
	public void setTransaction(MainController controller, Portfolio p) {
		this.p = p.clone();
		this.op = p;
		this.controller = controller;
		initValues();
	}

	/**
	 * Initializes the record controller.
	 * 
	 * @param location
	 *            The location.
	 * @param resources
	 *            The resources.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		assert run != null : "fx:id=\"run\" was not injected: check your FXML file.";
		run.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				do_simulate();
			}
		});

		assert restart != null : "fx:id=\"restart\" was not injected: check your FXML file.";
		restart.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				p = p.clone();
				updateView();
			}
		});
	}
	
	public void do_simulate() {
		//TODO do simulation
		//p = s.simulate(steps(), step_size(), p);
	}

	/**
	 * Sets the initial values of the components.
	 */
	private void initValues() {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reset the values of the components.
	 */
	public void updateView() {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
