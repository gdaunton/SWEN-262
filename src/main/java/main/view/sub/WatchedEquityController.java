package main.view.sub;


import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import main.model.holdings.WatchedEquity;
import main.view.elements.DoubleTextField;

import java.net.URL;
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

    /**
     * Sets the record.
     *
     * @param equity The watched equity item.
     */
    public void setWatchedEquity(WatchedEquity equity) {
        this.equity = equity;
        if (equity.highTrigger != -1)
            high_bound.setText(Double.toString(equity.highTrigger));
        if (equity.lowTrigger != -1)
            low_bound.setText(Double.toString(equity.lowTrigger));

        ObservableList<XYChart.Series<Date, Number>> series = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Date, Number>> series1 = FXCollections.observableArrayList();
        for (WatchedEquity.TriggerNode t : equity.triggerNodes)
            series1.add(new XYChart.Data<>(t.timeStamp, t.ppshare));
        series.add(new XYChart.Series<>("Equity", series1));
        highMarker.setStroke(Color.GREEN);
        lowMarker.setStroke(Color.RED);
        chart.getData().addListener((ListChangeListener<XYChart.Series<Date, Number>>) c -> updateLines(equity.highTrigger, equity.lowTrigger));
        chart.setData(series);
    }

    /**
     * Update the min/max lines
     * @param highTrigger the max line value
     * @param lowTrigger the min line value
     */
    private void updateLines(double highTrigger, double lowTrigger) {
        Node chartArea = chart.lookup(".chart-plot-background");
        Bounds chartAreaBounds = chartArea.getBoundsInLocal();
        // remember scene position of chart area
        double yShift = chartArea.getLayoutY() + 5;
        double xShift = chartArea.getLayoutX() + 5;
        // set x parameters of the valueMarker to chart area bounds
        highMarker.setStartX(xShift);
        highMarker.setEndX(xShift + chartAreaBounds.getMaxX());
        lowMarker.setStartX(xShift);
        lowMarker.setEndX(xShift + chartAreaBounds.getMaxX());

        double highPosition = chart.getYAxis().getDisplayPosition(highTrigger);
        double lowPosition = chart.getYAxis().getDisplayPosition(lowTrigger);
        highMarker.setStartY(yShift + highPosition);
        highMarker.setEndY(yShift + highPosition);
        lowMarker.setStartY(yShift + lowPosition);
        lowMarker.setEndY(yShift + lowPosition);

        if(chart_pane.getChildren().contains(highMarker)) {
            if (highPosition < 0)
                chart_pane.getChildren().remove(highMarker);
        } else {
            if (highPosition > 0)
                chart_pane.getChildren().add(highMarker);
        }

        if(chart_pane.getChildren().contains(lowMarker)) {
            if (lowPosition < 0)
                chart_pane.getChildren().remove(lowMarker);
        } else {
            if (lowPosition > 0)
                chart_pane.getChildren().add(lowMarker);
        }
    }

    /**
     * Initializes the record controller.
     *
     * @param location  The location.
     * @param resources The resources.
     */
    public void initialize(URL location, ResourceBundle resources) {
        apply.setOnAction(event -> {
            equity.highTrigger = high_bound.getDouble();
            equity.lowTrigger = low_bound.getDouble();
            updateLines(equity.highTrigger, equity.lowTrigger);
            if(equity.highTrigger != -1)
                high_bound.setValue(equity.highTrigger);
            if(equity.highTrigger != -1)
                low_bound.setValue(equity.lowTrigger);
        });
        cancel.setOnAction(event -> {
            updateLines(equity.highTrigger, equity.lowTrigger);
            if(equity.highTrigger != -1)
                high_bound.setValue(equity.highTrigger);
            if(equity.highTrigger != -1)
                low_bound.setValue(equity.lowTrigger);
        });

        high_bound.textProperty().addListener((observable, oldValue, newValue) -> {
            apply.setDisable(Double.parseDouble(newValue) == equity.highTrigger);
        });
        low_bound.textProperty().addListener((observable, oldValue, newValue) -> {
            apply.setDisable(Double.parseDouble(newValue) == equity.lowTrigger);
        });

        chart.boundsInParentProperty().addListener((ov, old, newBounds) -> {
            updateLines(equity.highTrigger, equity.lowTrigger);
        });
        chart.setAnimated(false);
    }
}
