package main.view.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.controller.command.HoldingCommand;
import main.model.holdings.Account;
import main.view.MainController;

public class AccountController implements Initializable, DialogController {

    @FXML
    private TextField name;
    @FXML
    private TextField balance;
    @FXML
    private ChoiceBox<Account.Type> type;
    @FXML
    private Button cancel;
    @FXML
    private Button apply;
    @FXML
    private Text error;

    private Stage stage;
    private MainController controller;

    /**
     * Sets the main controller.
     *
     * @param controller The controller.
     */
    public void setController(MainController controller) {
        this.controller = controller;
    }

    /**
     * Initializes the account controller.
     *
     * @param location  The location.
     * @param resources The resources.
     */
    public void initialize(URL location, ResourceBundle resources) {
        type.getItems().setAll(Account.Type.values());
        cancel.setOnAction(event -> stage.close());
        apply.setOnAction(event -> {
            if (!name.getText().isEmpty() && !balance.getText().isEmpty() && !type.getSelectionModel().isEmpty()) {
                for (Account a : controller.getAccounts()) {
                    if (a.getName().equals(name.getText())) {
                        error.setVisible(true);
                        error.setText("An account with that name already exists");
                        return;
                    }
                }
                Account account = new Account(name.getText(), Double.parseDouble(balance.getText()),
                        type.getValue());
                controller.sendCommand(HoldingCommand.Action.ADD, account);
            }
            stage.close();
        });
    }

    /**
     * Sets the stage.
     *
     * @param stage The stage.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
