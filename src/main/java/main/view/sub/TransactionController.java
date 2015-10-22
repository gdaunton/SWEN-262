package main.view.sub;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import main.model.Transaction;
import main.view.MainController;


public class TransactionController implements Initializable{

    @FXML
    private TableView<Transaction> table;

    private MainController controller;

    /**
     * Sets the record.
     *
     * @param controller   The controller.
     * @param transactions The transactions.
     */
    public void setTransaction(MainController controller, ArrayList<Transaction> transactions) {
        this.controller = controller;
        TableColumn type = new TableColumn("Transaction Type");
        type.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Transaction, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Transaction, String> t) {
                return new SimpleStringProperty(t.getValue().type);
            }
        });
        TableColumn holding = new TableColumn("Holding(s)");
        holding.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Transaction, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Transaction, String> t) {
                return new SimpleStringProperty(t.getValue().holding);
            }
        });
        TableColumn value = new TableColumn("Value");
        value.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Transaction, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Transaction, String> t) {
                return new SimpleStringProperty(t.getValue().amount);
            }
        });
        TableColumn date = new TableColumn("Date");
        date.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Transaction, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Transaction, String> t) {
                return new SimpleStringProperty(t.getValue().date);
            }
        });
        if(transactions != null)
            table.setItems(FXCollections.observableArrayList(transactions));
        table.getColumns().addAll(type, holding, value, date);
    }

    /**
     * Initializes the record controller.
     *
     * @param location  The location.
     * @param resources The resources.
     */
    public void initialize(URL location, ResourceBundle resources) {

    }
}
