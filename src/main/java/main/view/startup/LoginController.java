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

public class LoginController implements Initializable {

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

    /**
     * Sets the FPTS app.
     *
     * @param app The app.
     */
    public void setApp(FPTS app) {
        this.app = app;
    }

    /**
     * Initializes the login controller.
     *
     * @param location  The location.
     * @param resources The resources.
     */
    public void initialize(URL location, ResourceBundle resources) {
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file.";
        loginButton.setOnAction(event -> {
            if (app == null) {
                errorText.setVisible(true);
                errorText.setText("Hello " + username.getText());
            } else {
                try {
                    if (!app.handleLogin(username.getText(), password.getText()))
                        errorText.setText("User does not exist");
                    errorText.setVisible(true);
                } catch (UserManager.InvalidPasswordException e) {
                    errorText.setText(e.getMessage());
                    errorText.setVisible(true);
                } catch (FPTS.UnassociatedUserException e1) {
                    errorText.setText(e1.getMessage());
                    errorText.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        assert createUser != null : "fx:id=\"createUser\" was not injected: check your FXML file.";
        createUser.setOnAction(event -> app.gotoCreateUser());

        assert createPortfolio != null : "fx:id=\"createPortfolio\" was not injected: check your FXML file.";
        createPortfolio.setOnAction(event -> app.gotoCreatePortfolio());
    }
}
