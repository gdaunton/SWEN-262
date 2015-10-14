package main.view.dialog;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.controller.command.HoldingCommand;
import main.model.holdings.Account;
import main.model.holdings.Equity;
import main.view.MainController;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class EquityController implements Initializable, DialogController{

    @FXML
    private TextField shares;
    @FXML
    private Pane purchase;
    @FXML
    private ListView<Account> accounts;
    @FXML
    private CheckBox outside;
    @FXML
    private Button cancel;
    @FXML
    private Button apply;
    @FXML
    private ListView<Equity> equity_list;
    @FXML
    private TextField transaction_total;

    private Stage stage;
    private MainController controller;
    private ArrayList<OnTransactionListener> transactionListeners;

    public void setController(MainController controller) {
        this.controller = controller;
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
                purchase.setDisable(true);
                apply.setDisable(true);
                cancel.setDisable(true);
                shares.setText("");
                stage.close();
            }
        });

        apply.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                purchase.setDisable(true);
                apply.setDisable(true);
                cancel.setDisable(true);
                if(!equity_list.getSelectionModel().isEmpty() && (!accounts.getSelectionModel().isEmpty() || outside.isSelected())) {
                    Equity equity = equity_list.getSelectionModel().getSelectedItem();
                    controller.sendCommand(HoldingCommand.Action.ADD, equity);
                    if (!outside.isSelected()) {
                        double transaction = equity.getValue() - (equity.getPrice_per_share() * Integer.parseInt(shares.getText()));
                        if (transaction < 0)
                            controller.sendCommand(HoldingCommand.Action.MODIFY, getSelectedAccount(), HoldingCommand.Modification.WITHDRAW, Math.abs(transaction));
                        else
                            controller.sendCommand(HoldingCommand.Action.MODIFY, getSelectedAccount(), HoldingCommand.Modification.DEPOSIT, transaction);
                    }
                }
                stage.close();
            }
        });
    }

    private void initValues() {
        equity_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Equity>() {
            public void changed(ObservableValue<? extends Equity> observable, Equity oldValue, Equity newValue) {

            }
        });
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
        return accounts.getSelectionModel().getSelectedItem();
    }

    private void showTransaction(int value) {
        Equity equity = equity_list.getSelectionModel().getSelectedItem();
        double transaction_value = equity.getValue()-(equity.getPrice_per_share()*value);
        transaction_total.setText(Double.toString(transaction_value));
        for(OnTransactionListener l : transactionListeners)
            l.update(transaction_value);
    }

    public void registerCellTransactionListener(OnTransactionListener transactionListener) {
        transactionListeners.add(transactionListener);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
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
