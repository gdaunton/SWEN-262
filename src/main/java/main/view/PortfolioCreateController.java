package main.view;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.FPTS;

import java.net.URL;
import java.util.ResourceBundle;


public class PortfolioCreateController implements Initializable{

    @FXML
    private TextField name;
    @FXML
    private Button importButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button createButton;

    private FPTS app;

    public void setApp(FPTS app){
        this.app = app;
    }

    public void initialize(URL location, ResourceBundle resources) {
        assert importButton != null : "fx:id=\"importButton\" was not injected: check your FXML file.";
        importButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //TODO Do file selection and then pass to csv importer
            }
        });

        assert createButton != null : "fx:id=\"createButton\" was not injected: check your FXML file.";
        createButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //TODO Send a create command to create a new portfolio
            }
        });

        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file.";
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
               app.gotoLogin();
            }
        });
    }

}
