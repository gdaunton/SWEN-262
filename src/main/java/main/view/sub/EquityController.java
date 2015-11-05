package main.view.sub;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import main.controller.command.HoldingCommand;
import main.model.holdings.Account;
import main.model.holdings.Equity;
import main.view.MainController;
import main.view.elements.IntegerTextField;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EquityController implements Initializable {

    @FXML
    private TextField name;
    @FXML
    private TextField ticker;
    @FXML
    private TextField ppshare;
    @FXML
    private IntegerTextField shares;
    @FXML
    private ListView sectors;
    @FXML
    private TextField total;
    @FXML
    private TextField transaction_total;
    @FXML
    private Button apply;
    @FXML
    private ListView<Account> accounts;
    @FXML
    private CheckBox outside;
    @FXML
    private Button cancel;

    private Equity equity;
    private MainController controller;
    private ArrayList<OnTransactionListener> transactionListeners;

    /**
     * Sets the equity.
     *
     * @param controller The controller.
     * @param equity     The equity.
     */
    public void setEquity(MainController controller, Equity equity) {
        this.controller = controller;
        this.equity = equity;
        initValues();
    }

    /**
     * Initializes the equity controller.
     *
     * @param location  The location.
     * @param resources The resources.
     */
    public void initialize(URL location, ResourceBundle resources) {
        transactionListeners = new ArrayList<OnTransactionListener>();
        shares.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.equals("") && shares.getInteger() != equity.getShares()) {
                    showTransaction(shares.getInteger());
                    cancel.setDisable(false);
                } else {
                    setDisabledElements(true);
                    transaction_total.setText("");
                }
            }
        });

        outside.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (outside.isSelected()) {
                    apply.setDisable(false);
                    accounts.setDisable(true);
                    accounts.getSelectionModel().clearSelection();
                } else {
                    apply.setDisable(true);
                    accounts.setDisable(false);
                }
            }
        });
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                setDisabledElements(true);
                apply.setDisable(true);
                cancel.setDisable(true);
                shares.setText(Integer.toString(equity.getShares()));
                transaction_total.setText("");
            }
        });

        apply.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                setDisabledElements(true);
                apply.setDisable(true);
                cancel.setDisable(true);
                if (!outside.isSelected()) {
                    double transaction = equity.getValue() - (equity.getPrice_per_share() * shares.getInteger());
                    if (transaction < 0)
                        controller.sendCommand(HoldingCommand.Action.MODIFY,
                                accounts.getSelectionModel().getSelectedItem(), HoldingCommand.Modification.WITHDRAW,
                                Math.abs(transaction));
                    else
                        controller.sendCommand(HoldingCommand.Action.MODIFY,
                                accounts.getSelectionModel().getSelectedItem(), HoldingCommand.Modification.DEPOSIT,
                                transaction);
                }
                controller.sendCommand(HoldingCommand.Action.MODIFY, equity, HoldingCommand.Modification.SHARES,
                        Double.parseDouble(shares.getText()));
                transaction_total.setText("");
            }
        });
    }

    /**
     * Sets the initial values.
     */
    private void initValues() {
        try {
            name.setText(equity.getName());
            ticker.setText(equity.getTickerSymbol());
            ppshare.setText(Double.toString(equity.getPrice_per_share()));
            shares.setText(Integer.toString(equity.getShares()));
            total.setText(NumberFormat.getCurrencyInstance().format(equity.getValue()));

            sectors.setItems(FXCollections.observableArrayList(equity.getMarketSectors()));
            sectors.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if (!shares.getText().equals(""))
                        showTransaction(shares.getInteger());
                }
            });
            accounts.setItems(FXCollections.observableArrayList(controller.getAccounts()));
            accounts.setCellFactory(new Callback<ListView<Account>, ListCell<Account>>() {
                public ListCell<Account> call(ListView<Account> list) {
                    return new AccountCell(EquityController.this);
                }
            });
            accounts.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Account>() {
                public void changed(ObservableValue observable, Account oldValue, Account newValue) {
                    if (newValue != null) {
                        double transaction = equity.getValue() - (equity.getPrice_per_share() * shares.getInteger());
                        if (newValue.getBalance() + transaction > 0 && !shares.getText().equals("")) {
                            apply.setDisable(false);
                        } else
                            apply.setDisable(true);
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    /**
     * Shows the transaction.
     *
     * @param value Index of the transaction.
     */
    private void showTransaction(int value) {
        setDisabledElements(false);
        double transaction_value = equity.getValue() - (equity.getPrice_per_share() * value);
        if (transaction_value < 0)
            transaction_total.setText("-" + NumberFormat.getCurrencyInstance().format(transaction_value * (-1)));
        else
            transaction_total.setText(NumberFormat.getCurrencyInstance().format(transaction_value));
        if (!accounts.getSelectionModel().isEmpty()) {
            if (accounts.getSelectionModel().getSelectedItem().getBalance() + transaction_value > 0) {
                apply.setDisable(false);
            } else
                apply.setDisable(true);
        }
        for (OnTransactionListener l : transactionListeners)
            l.update(transaction_value);
    }

    /**
     * Sets disabled elements.
     *
     * @param disabled True to disable; false otherwise.
     */
    private void setDisabledElements(boolean disabled) {
        accounts.setDisable(disabled);
        outside.setDisable(disabled);
    }

    /**
     * Registers a listener.
     *
     * @param transactionListener The listener.
     */
    public void registerCellTransactionListener(OnTransactionListener transactionListener) {
        transactionListeners.add(transactionListener);
    }

    /**
     * The class for the accounts listview. Will update based upon the accounts capacity to foot bill for the current transaction.
     */
    private class AccountCell extends ListCell<Account> implements OnTransactionListener {
        private Account item;

        public AccountCell(EquityController parent) {
            parent.registerCellTransactionListener(this);
        }

        public void update(double value) {
            setText(item == null ? ""
                    : item.getName() + "    " + NumberFormat.getCurrencyInstance().format(item.getBalance()));
            if (item != null) {
                double transaction_value = item.getBalance() + value;
                setTextFill(transaction_value < 0 ? Color.RED : Color.BLACK);
            }
        }

        @Override
        protected void updateItem(Account item, boolean empty) {
            super.updateItem(item, empty);
            this.item = item;
            if (!empty) {
                setText(item == null ? ""
                        : item.getName() + "    " + NumberFormat.getCurrencyInstance().format(item.getBalance()));
                if (item != null) {
                    double value = item.getBalance();
                    setTextFill(value < 0 ? Color.RED : Color.BLACK);
                }
            }
        }
    }

    private interface OnTransactionListener {
        void update(double value);
    }
}
