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
import javafx.scene.paint.Color;
import javafx.util.Callback;
import main.controller.command.HoldingCommand;
import main.model.holdings.Account;
import main.view.MainController;
import main.view.custom.CustomComboBox;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AccountController implements Initializable{

    @FXML
    private TextField name;
    @FXML
    private TextField balance;
    @FXML
    private TextField date;
    @FXML
    private CustomComboBox<Account> destination;
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
            }
        });
        action.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                showTransaction();
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

    private void showTransaction() {
        apply.setDisable(false);
        cancel.setDisable(false);
        transaction.setDisable(false);
        final double transaction_value;
        if(action.getSelectedToggle().equals(withdraw))
            transaction_value = (-1) * Double.parseDouble(transaction_total.getText());
        else if(action.getSelectedToggle().equals(deposit))
            transaction_value = Double.parseDouble(transaction_total.getText());
        else
            transaction_value = Double.parseDouble(transaction_total.getText());
        transaction_total.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                post.setText(Double.toString(account.getBalance() + transaction_value));
            }
        });
        transaction_total.setText(Double.toString(transaction_value));
    }
}
