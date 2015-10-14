package main.view.dialog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.text.Text;
import main.controller.command.HoldingCommand;
import main.model.holdings.Account;
import main.view.MainController;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountController {
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

    private MainController controller;

    public void setController(MainController controller) {
        this.controller = controller;
    }

    public void initialize(URL location, ResourceBundle resources) {
        type.getItems().setAll(Account.Type.values());
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

            }
        });
        apply.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if(!name.getText().isEmpty() && !balance.getText().isEmpty() && !type.getSelectionModel().isEmpty()) {
                    for(Account a : controller.getAccounts()){
                        if(a.getName().equals(name.getText())){
                            error.setVisible(true);
                            error.setText("An account with that name already exists");
                            return;
                        }
                    }
                    Account account = new Account(name.getText(), Double.parseDouble(balance.getText()), type.getValue());
                    controller.sendCommand(HoldingCommand.Action.ADD, account);
                }
            }
        });
    }
}
