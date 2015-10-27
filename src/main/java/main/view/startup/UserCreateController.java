package main.view.startup;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import main.FPTS;
import main.model.user.UserManager;

public class UserCreateController implements Initializable {

    @FXML
    private Button createButton;
    @FXML
    private Button cancelPortfolio;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField password_repeat;
    @FXML
    private Text errorText;

    private FPTS app;

    public void setApp(FPTS app) {
        this.app = app;
    }

    /**
     * Initializes the user creator.
     *
     * @param location  The location.
     * @param resources The resources.
     */
    public void initialize(URL location, ResourceBundle resources) {
        assert createButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file.";
        createButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (app == null) {
                    errorText.setVisible(true);
                    errorText.setText("Hello " + username.getText());
                } else if (password.getText().equals(password_repeat.getText())) {
                    try {
                        if (!app.createUser(username.getText(), password.getText()))
                            errorText.setText("User could not be created");
                        errorText.setVisible(true);
                    } catch (UserManager.UsernameOccupiedException e) {
                        errorText.setText("That username is Taken");
                        errorText.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    errorText.setVisible(true);
                    errorText.setText("Passwords do not match");
                }
            }
        });

        assert cancelPortfolio != null : "fx:id=\"createPortfolio\" was not injected: check your FXML file.";
        cancelPortfolio.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                app.gotoLogin();
            }
        });
    }
}
