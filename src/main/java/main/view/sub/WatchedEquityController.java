package main.view.sub;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.util.Callback;
import main.model.holdings.Transaction;
import main.model.holdings.WatchedEquity;
import main.view.MainController;
import main.view.elements.DateAxis;
import main.view.elements.DoubleTextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;


public class WatchedEquityController implements Initializable {

    @FXML
    private LineChart<Date, Number> chart;
    @FXML
    private Button apply;
    @FXML
    private Button cancel;
    @FXML
    private DoubleTextField high_bound;
    @FXML
    private DoubleTextField low_bound;
    @FXML
    private Pane chart_pane;

    private WatchedEquity equity;
    private Line highMarker = new Line();
    private Line lowMarker = new Line();
    private MainController controller;
    private double yShift;

    /**
     * Sets the record.
     *
     * @param controller   The controller.
     * @param equity The watched equity item.
     */
    public void setWatchedEquity(MainController controller, WatchedEquity equity) {
        this.controller = controller;
        this.equity = equity;
        if(equity.highTrigger != -1)
            high_bound.setText(Double.toString(equity.highTrigger));
        if(equity.lowTrigger != -1)
            low_bound.setText(Double.toString(equity.lowTrigger));

        ObservableList<XYChart.Series<Date, Number>> series = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Date, Number>> series1 = FXCollections.observableArrayList();
        for(WatchedEquity.TriggerNode t : equity.triggerNodes)
            series1.add(new XYChart.Data<Date, Number>(t.timeStamp, t.ppshare));
        series.add(new XYChart.Series<>("Equity", series1));
        chart.setData(series);
        initMinMaxLine(equity.highTrigger, equity.lowTrigger);
    }

    private void initMinMaxLine(double highTrigger, double lowTrigger) {
        Node chartArea = chart.lookup(".chart-plot-background");
        Bounds chartAreaBounds = chartArea.localToScene(chartArea.getBoundsInLocal());
        // remember scene position of chart area
        yShift = chartAreaBounds.getMinY();
        // set x parameters of the valueMarker to chart area bounds
        highMarker.setStartX(chartAreaBounds.getMinX());
        highMarker.setEndX(chartAreaBounds.getMaxX());
        lowMarker.setStartX(chartAreaBounds.getMinX());
        lowMarker.setEndX(chartAreaBounds.getMaxX());
        chart_pane.getChildren().addAll(highMarker, lowMarker);
        updateLines(highTrigger, lowTrigger);
    }

    private void updateLines(double highTrigger, double lowTrigger) {
        double highPosition = chart.getYAxis().getDisplayPosition(highTrigger);
        double lowPosition = chart.getYAxis().getDisplayPosition(lowTrigger);
        highMarker.setStartY(yShift + highPosition);
        highMarker.setEndY(yShift + highPosition);
        lowMarker.setStartY(yShift + lowPosition);
        lowMarker.setEndY(yShift + lowPosition);
        if(equity.highTrigger == -1)
            highMarker.setVisible(false);
        else
            highMarker.setVisible(true);
        if(equity.lowTrigger == -1)
            lowMarker.setVisible(false);
        else
            lowMarker.setVisible(true);
        lowMarker.toFront();
        highMarker.toFront();
    }

    /**
     * Initializes the record controller.
     *
     * @param location  The location.
     * @param resources The resources.
     */
    public void initialize(URL location, ResourceBundle resources) {
        apply.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                equity.highTrigger = high_bound.getDouble();
                equity.lowTrigger = low_bound.getDouble();
                updateLines(equity.highTrigger, equity.lowTrigger);
                if(equity.highTrigger != -1)
                    high_bound.setText(Double.toString(equity.highTrigger));
                if(equity.highTrigger != -1)
                    low_bound.setText(Double.toString(equity.lowTrigger));
            }
        });
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateLines(equity.highTrigger, equity.lowTrigger);
                if(equity.highTrigger != -1)
                    high_bound.setText(Double.toString(equity.highTrigger));
                if(equity.highTrigger != -1)
                    low_bound.setText(Double.toString(equity.lowTrigger));
            }
        });
        high_bound.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                apply.setDisable(newValue.equals(equity.highTrigger));
            }
        });
        low_bound.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                apply.setDisable(newValue.equals(equity.lowTrigger));
            }
        });
    }
}
