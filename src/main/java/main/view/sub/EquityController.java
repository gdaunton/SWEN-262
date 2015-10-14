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
import main.model.holdings.Equity;
import main.view.MainController;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class EquityController implements Initializable{

    @FXML
    private TextField name;
    @FXML
    private TextField ticker;
    @FXML
    private TextField ppshare;
    @FXML
    private TextField shares;
    @FXML
    private ListView sectors;
    @FXML
    private TextField total;
    @FXML
    private Pane purchase;
    @FXML
    private TextField transaction_total;
    @FXML
    private Button apply;
    @FXML
    private ListView accounts;
    @FXML
    private CheckBox outside;
    @FXML
    private Button cancel;

    private Equity equity;
    private MainController controller;
    private ArrayList<OnTransactionListener> transactionListeners;

    public void setEquity(MainController controller, Equity equity){
        this.controller = controller;
        this.equity = equity;
        initValues();
    }

    public void initialize(URL location, ResourceBundle resources) {
        shares.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.matches("\\d+")) {
                    try {
                        int value = Integer.parseInt(newValue);
                        if (value < 0)
                            shares.setText(oldValue);
                        else {
                            showTransaction(value);
                        }
                    } catch (Exception e){
                        shares.setText(oldValue);
                    }
                } else {
                    shares.setText(oldValue);
                }
            }
        });

        outside.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (outside.isSelected()) {
                    accounts.setDisable(true);
                    accounts.getSelectionModel().clearSelection();
                } else {
                    accounts.setDisable(false);
                }
            }
        });
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
            purchase.setVisible(false);
            purchase.setDisable(true);
            apply.setDisable(true);
            cancel.setDisable(true);
            shares.setText(Integer.toString(equity.getShares()));
            }
        });

        apply.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                purchase.setVisible(false);
                purchase.setDisable(true);
                apply.setDisable(true);
                cancel.setDisable(true);
                controller.sendCommand(HoldingCommand.Action.MODIFY, equity, HoldingCommand.Modification.SHARES, Double.parseDouble(shares.getText()));
                if(!outside.isSelected()) {
                    double transaction = equity.getValue() - (equity.getPrice_per_share() * Integer.parseInt(shares.getText()));
                    if (transaction < 0)
                        controller.sendCommand(HoldingCommand.Action.MODIFY, getSelectedAccount(), HoldingCommand.Modification.WITHDRAW, Math.abs(transaction));
                    else
                        controller.sendCommand(HoldingCommand.Action.MODIFY, getSelectedAccount(), HoldingCommand.Modification.DEPOSIT, transaction);
                }
            }
        });
    }

    private void initValues() {
        name.setText(equity.getName());
        ticker.setText(equity.getTickerSymbol());
        ppshare.setText(Double.toString(equity.getPrice_per_share()));
        shares.setText(Integer.toString(equity.getShares()));
        total.setText(Double.toString(equity.getValue()));

        sectors.setItems(FXCollections.observableArrayList(equity.getMarketSectors()));

        accounts = new ListView<Account>(FXCollections.observableArrayList(controller.getAccounts()));
        accounts.setCellFactory(new Callback<ListView<Account>, ListCell<Account>>() {
            public ListCell<Account> call(ListView<Account> list) {
                return new AccountCell(EquityController.this);
            }
        });
        accounts.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Account>() {
            public void changed(ObservableValue observable, Account oldValue, Account newValue) {
                double transaction = Double.parseDouble(transaction_total.getText()) * (-1);
                if(newValue.getBalance() > transaction)
                    accounts.getSelectionModel().clearSelection();
            }
        });
    }

    private Account getSelectedAccount() {
        return controller.getAccounts().get(accounts.getSelectionModel().getSelectedIndex());
    }

    private void showTransaction(int value) {
        apply.setDisable(false);
        cancel.setDisable(false);
        purchase.setVisible(true);
        purchase.setDisable(true);
        double transaction_value = equity.getValue()-(equity.getPrice_per_share()*value);
        transaction_total.setText(Double.toString(transaction_value));
        for(OnTransactionListener l : transactionListeners)
            l.update(transaction_value);
    }

    public void registerCellTransactionListener(OnTransactionListener transactionListener) {
        transactionListeners.add(transactionListener);
    }

    private class AccountCell extends ListCell<Account> implements OnTransactionListener{
        private double transaction_value;

        public AccountCell(EquityController parent) {
            parent.registerCellTransactionListener(this);
            this.transaction_value = 0;
        }

        public void update(double value) {
            transaction_value = value;
        }

        @Override
        protected void updateItem(Account item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : item.getName() + "    " + NumberFormat.getCurrencyInstance().format(item.getBalance()));

            if (item != null) {
                double value = item.getBalance() + transaction_value;
                setTextFill(isSelected() ? Color.WHITE : value < 0 ? Color.RED : Color.BLACK);
            }
        }
    }

    private interface OnTransactionListener {
        void update(double value);
    }
}
