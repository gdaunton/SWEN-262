package main.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import main.FPTS;
import main.model.UserManager;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController  implements Initializable {

    @FXML
    private Button loginButton;
    @FXML
    private Button createPortfolio;
    @FXML
    private Button createUser;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Text errorText;

    private FPTS app;

    public void setApp(FPTS app){
        this.app = app;
    }

    public void initialize(URL location, ResourceBundle resources) {
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file.";
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (app == null){
                    errorText.setVisible(true);
                    errorText.setText("Hello " + username.getText());
                } else {
                    try {
                        if(!app.handleLogin(username.getText(), password.getText()))
                            errorText.setText("User does not exist");
                        errorText.setVisible(true);
                    } catch(UserManager.InvalidPasswordException e) {
                        errorText.setText("Password is incorrect");
                        errorText.setVisible(true);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        assert createUser != null : "fx:id=\"createUser\" was not injected: check your FXML file.";
        createUser.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                app.gotoCreateUser();
            }
        });

        assert createPortfolio != null : "fx:id=\"createPortfolio\" was not injected: check your FXML file.";
        createPortfolio.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                app.gotoCreatePortfolio();
            }
        });
    }
}
