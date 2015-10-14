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
import org.apache.commons.codec.binary.StringUtils;

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
    private ComboBox<Account> destination;
    @FXML
    private TextField post;
    @FXML
    private ToggleGroup action;
    @FXML
    private RadioButton withdraw;
    @FXML
    private RadioButton deposit;
    @FXML
    private RadioButton transfer;
    @FXML
    private ChoiceBox<Account.Type> type;
    @FXML
    private TextField transaction_total;
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
                disableElements(true);
                apply.setDisable(true);
                cancel.setDisable(true);
                transaction_total.setText("");
                action.selectToggle(null);
            }
        });
        apply.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                disableElements(true);

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
                    disableElements(false);
                    if(!action.getSelectedToggle().equals(transfer))
                        destination.setDisable(true);
                    transaction_total.textProperty().addListener(new ChangeListener<String>() {
                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            if(!newValue.equals(""))
                                showTransaction(Double.parseDouble(newValue));
                            else
                                post.setText("");
                        }
                    });
                } else {
                    disableElements(true);
                }
            }
        });
    }

    private void disableElements(boolean disabled){
        transaction_total.setDisable(disabled);
        destination.setDisable(disabled);
    }

    private void initValues() {
        try {
            if (controller.getAccounts().size() < 2)
                transfer.setDisable(true);
            name.setText(account.getName());
            balance.setText(NumberFormat.getCurrencyInstance().format(account.getBalance()));
            date.setText(account.getOpened().toString());
            type.getItems().setAll(Account.Type.values());
            type.setValue(account.getType());
            ArrayList<Account> temp = new ArrayList<Account>();
            for(Account a : controller.getAccounts())
                if(a != this.account)
                    temp.add(a);
            destination.setItems(FXCollections.observableArrayList(temp));
        } catch (Exception e){}
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
            transaction_value = (-1) * value;
        post.setText(NumberFormat.getCurrencyInstance().format(account.getBalance() + transaction_value));
    }
}
