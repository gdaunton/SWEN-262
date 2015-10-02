package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController  implements Initializable {

    @FXML
    private Button loginButton;
    @FXML
    private Button createPortfolio;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    public void initialize(URL location, ResourceBundle resources) {
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file.";
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //TODO Handle user login
            }
        });

        assert createPortfolio != null : "fx:id=\"createPortfolio\" was not injected: check your FXML file.";
        createPortfolio.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //TODO Figure out the best way to move to the Portfolio create scene
            }
        });
    }
}
