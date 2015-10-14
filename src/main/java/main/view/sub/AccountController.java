package main.view.sub;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import main.controller.command.HoldingCommand;
import main.model.holdings.Account;
import main.view.MainController;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountController implements Initializable{

    @FXML
    private TextField name;
    @FXML
    private TextField balance;
    @FXML
    private TextField date;
    @FXML
    private ComboBox<Account> destination;
    @FXML
    private TextField post;
    @FXML
    private ToggleGroup action;
    @FXML
    private Toggle withdraw;
    @FXML
    private Toggle deposit;
    @FXML
    private Toggle transfer;
    @FXML
    private ChoiceBox type;
    @FXML
    private TextField transaction_total;
    @FXML
    private Pane transaction;
    @FXML
    private Button cancel;
    @FXML
    private Button apply;


    private Account account;
    private MainController controller;

    public void setAccount(MainController controller, Account account){
        this.account = account;
        this.controller = controller;
        initValues();
    }

    public void initialize(URL location, ResourceBundle resources) {
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                transaction.setDisable(true);
                apply.setDisable(true);
                cancel.setDisable(true);
                transaction_total.setText("");
                action.selectToggle(null);
            }
        });
        apply.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                transaction.setDisable(true);
                apply.setDisable(true);
                cancel.setDisable(true);
                double value = Double.parseDouble(transaction_total.getText());
                if(action.getSelectedToggle().equals(withdraw))
                    controller.sendCommand(HoldingCommand.Action.MODIFY, account, HoldingCommand.Modification.WITHDRAW, value);
                else if(action.getSelectedToggle().equals(deposit))
                    controller.sendCommand(HoldingCommand.Action.MODIFY, account, HoldingCommand.Modification.DEPOSIT, value);
                else if(action.getSelectedToggle().equals(transfer))
                    controller.sendCommand(HoldingCommand.Action.MODIFY, account, destination.getValue(), HoldingCommand.Modification.TRANSFER, value);
                transaction_total.setText("");
                action.selectToggle(null);
            }
        });
        action.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(action.getSelectedToggle() != null) {
                    transaction.setDisable(false);
                    transaction_total.textProperty().addListener(new ChangeListener<String>() {
                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            if (newValue.matches("\\d+")) {
                                try {
                                    int value = Integer.parseInt(newValue);
                                    if (value < 0)
                                        transaction_total.setText(oldValue);
                                    else {
                                        showTransaction(value);
                                    }
                                } catch (Exception e){
                                    transaction_total.setText(oldValue);
                                }
                            } else {
                                transaction_total.setText(oldValue);
                            }
                        }
                    });
                }else {
                    transaction.setDisable(true);
                }
            }
        });
    }

    private void initValues() {
        name.setText(account.getName());
        balance.setText(Double.toString(account.getBalance()));
        date.setText(account.getOpened().toString());
        type.setValue(account.getType());
        destination.setItems(FXCollections.observableArrayList(controller.getAccounts()));
    }

    private void showTransaction(double value) {
        apply.setDisable(false);
        cancel.setDisable(false);
        final double transaction_value;
        if(action.getSelectedToggle().equals(withdraw))
            transaction_value = (-1) * value;
        else if(action.getSelectedToggle().equals(deposit))
            transaction_value = value;
        else
            transaction_value = value;
        post.setText(Double.toString(account.getBalance() + transaction_value));
    }
}
