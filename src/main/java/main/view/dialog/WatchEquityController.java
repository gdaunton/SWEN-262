package main.view.dialog;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.controller.command.HoldingCommand;
import main.model.holdings.Account;
import main.model.holdings.Equity;
import main.model.holdings.HoldingManager;
import main.model.holdings.WatchedEquity;
import main.view.MainController;
import main.view.elements.DoubleTextField;

public class WatchEquityController implements Initializable, DialogController {

    @FXML
    private TableView<Equity> equity_list;
    @FXML
    private DoubleTextField high_bound;
    @FXML
    private DoubleTextField low_bound;
    @FXML
    private Button cancel;
    @FXML
    private Button apply;
    @FXML
    private TextField search;

    private Stage stage;
    private MainController controller;

    /**
     * Sets the main controller.
     *
     * @param controller The controller.
     */
    public void setController(MainController controller) {
        this.controller = controller;
        initValues();
    }

    /**
     * Initializes the account controller.
     *
     * @param location  The location.
     * @param resources The resources.
     */
    public void initialize(URL location, ResourceBundle resources) {
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                stage.close();
            }
        });
        apply.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                WatchedEquity temp = new WatchedEquity(equity_list.getSelectionModel().getSelectedItem().getTickerSymbol());
                if(!controller.getUser().watchedEquities.contains(temp)) {
                    temp.lowTrigger = low_bound.getDouble();
                    temp.lowTrigger = high_bound.getDouble();
                    controller.getUser().watchedEquities.add(temp);
                    controller.update();
                }
                stage.close();
            }
        });
        search.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        equity_list.getSelectionModel().clearSelection();
                    }
                });
                try {
                    equity_list.setItems(FXCollections.observableArrayList(getUnwatchedEquities(HoldingManager.searchAll(newValue, ""))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initValues() {
        apply.setDisable(true);
        TableColumn ticker = new TableColumn("Ticker");
        ticker.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Equity, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Equity, String> e) {
                return new SimpleStringProperty(e.getValue().getTickerSymbol());
            }
        });
        TableColumn name = new TableColumn("Name");
        name.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Equity, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Equity, String> e) {
                return new SimpleStringProperty(e.getValue().getName());
            }
        });
        TableColumn ppshare = new TableColumn("Price Per Share");
        ppshare.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Equity, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Equity, String> e) {
                return new SimpleStringProperty(Double.toString(e.getValue().getPrice_per_share()));
            }
        });
        equity_list.setItems(FXCollections.observableArrayList(getUnwatchedEquities(HoldingManager.equities_list)));
        equity_list.getColumns().addAll(ticker, name, ppshare);

        equity_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Equity>() {
            @Override
            public void changed(ObservableValue<? extends Equity> observable, Equity oldValue, Equity newValue) {
                apply.setDisable(equity_list.getSelectionModel().isEmpty());
            }
        });
    }

    private ArrayList<Equity> getUnwatchedEquities(ArrayList<Equity> list) {
        ArrayList<Equity> temp = new ArrayList<>();
        temp.addAll(list);
        ArrayList<WatchedEquity> watched = controller.getUser().watchedEquities;
        for(Equity e : list) {
            for(WatchedEquity we : watched) {
                if (e.getTickerSymbol().equals(we.getSymbol())) {
                    temp.remove(e);
                    break;
                }
            }
        }
        return temp;
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
