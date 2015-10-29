package main.view.simulation;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import main.controller.command.HoldingCommand;
import main.model.Portfolio;
import main.model.simulation.Bear;
import main.model.simulation.NoGrowth;
import main.model.simulation.Simulation;
import main.view.MainController;


public class BearController {

    //buttons
    @FXML
    private Button run;
    @FXML
    private Button restart;

    //configuration
    //dropdown for choosing the simulation length
	@FXML
	private ComboBox<Simulation.STEP_SIZE> sim_len = new ComboBox<Simulation.STEP_SIZE>();
    //textfield for rate of growth
    @FXML
    private TextField rate_field;
	//steps
    @FXML
    private TextField step_field;
	
    //TODO: portfolio display stuff


    //objects
    private Portfolio p;
    private Portfolio op;
    private MainController controller;
    private Bear s = new Bear();

    /**
     * Sets the record.
     *
     * @param controller The controller.
     * @param p          The portfolio.
     */
    public void setTransaction(MainController controller, Portfolio p) {
        this.op = p;
        this.p = op.clone();
        this.controller = controller;
        initValues();
    }

    /**
     * Initializes the record controller.
     *
     * @param location  The location.
     * @param resources The resources.
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
		
		sim_len.getItems().setAll(Simulation.STEP_SIZE.values());
    }

    public void do_simulate() {
        p = s.simulate(steps(), step_size(), p, rate());
    }

    private Simulation.STEP_SIZE step_size() {
        return (Simulation.STEP_SIZE) sim_len.getValue();
    }

    private int steps() {
        return Integer.parseInt(step_field.getText());
    }

    private double rate() {
        return Double.parseDouble(rate_field.getText());
    }

    /**
     * Sets the initial values of the components.
     */
    private void initValues() {
        try {
            sim_len.setValue(Simulation.STEP_SIZE.DAY);
            //TODO: initialize the portfolio view stuff
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reset the values of the components to match their original state - stored in "op"
     */
    public void resetView() {
        p = op.clone();
        updateView();
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