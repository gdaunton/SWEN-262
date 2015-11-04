package main.view.simulation;

import java.net.URL;
import java.util.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import main.model.Portfolio;
import main.model.holdings.Equity;
import main.model.holdings.Holding;
import main.model.simulation.Bear;
import main.model.simulation.Bull;
import main.model.simulation.NoGrowth;
import main.model.simulation.Simulation;
import main.view.MainController;
import main.view.elements.DoubleTextField;


public class SimulationController implements Initializable, OnNodeSelectedListener, Observer {

    @FXML
    private Button step;
    @FXML
    private Button reset;
    @FXML
    private ComboBox<Simulation.STEP_SIZE> step_size;
    @FXML
    private DoubleTextField rate;
    @FXML
    private LineChart<Date, Number> chart;
    @FXML
    private TableView<Holding> port_table;

    private ClickablePortfolioNode selected;
    private Portfolio op;
    private Simulation s;
    private ObservableList<XYChart.Data<Date, Portfolio>> data;

    /**
     * Sets the record.
     *
     * @param controller The controller.
     */
    public void setSimulation(MainController controller, Simulation sim) {
        this.s = sim;
        this.op = controller.getPortfolio();
        data = FXCollections.observableArrayList();
        initValues();
        if(sim instanceof NoGrowth)
            rate.setDisable(true);
        else
            rate.setValue(Simulation.DEFAULT_RATE);
    }

    /**
     * Initializes the record controller.
     *
     * @param location  The location.
     * @param resources The resources.
     */
    public void initialize(URL location, ResourceBundle resources) {
        step.setOnAction(event -> do_simulate());
        reset.setOnAction(event -> resetView());
        step_size.setItems(FXCollections.observableArrayList(Simulation.STEP_SIZE.values()));
        rate.setOnEnterPressedListener(() -> {
            updateData();
            rate.getParent().requestFocus();
        });
    }

    public void do_simulate() {
        data.add(new XYChart.Data<>(getNextDate(data.get(data.size() - 1).getXValue()), s.simulate(steps(), step_size(), data.get(data.size() - 1).getYValue().clone(), rate())));
        updateView();
    }

    private Date getNextDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        switch (step_size()) {
            case DAY:
                c.add(Calendar.DATE, 1);
                break;
            case MONTH:
                c.add(Calendar.DATE, 12);
                break;
            case YEAR:
                c.add(Calendar.DATE, 365);
                break;
        }
        return c.getTime();
    }

    private Simulation.STEP_SIZE step_size() {
        return step_size.getValue();
    }

    private int steps() {
        return data.size();
    }

    private double rate() {
        return rate.getDouble();
    }

    /**
     * Sets the initial values of the components.
     */
    private void initValues() {
        step_size.setValue(Simulation.STEP_SIZE.DAY);
        data.add(new XYChart.Data<>(Calendar.getInstance().getTime(), op.clone()));
        TableColumn name = new TableColumn("Holding Name");
        name.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Holding, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Holding, String> t) {
                return new SimpleStringProperty(t.getValue().toString());
            }
        });
        TableColumn shares = new TableColumn("Shares");
        shares.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Holding, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Holding, String> t) {
                if(t.getValue() instanceof Equity)
                    return new SimpleStringProperty(Double.toString(((Equity) t.getValue()).getShares()));
                else
                    return new SimpleStringProperty("");
            }
        });
        TableColumn ppshare = new TableColumn("Price Per Share");
        ppshare.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Holding, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Holding, String> t) {
                if(t.getValue() instanceof Equity)
                    return new SimpleStringProperty(Double.toString(((Equity) t.getValue()).getPrice_per_share()));
                else
                    return new SimpleStringProperty("");
            }
        });
        TableColumn value = new TableColumn("Value");
        value.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Holding, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Holding, String> t) {
                return new SimpleStringProperty(Double.toString(t.getValue().getValue()));
            }
        });
        if (selected != null)
            showPortfolioData(selected.getPortfolio());
        else
            showPortfolioData(data.get(0).getYValue());
        port_table.getColumns().addAll(name, shares, ppshare, value);
        updateView();
    }

    /**
     * Reset the values of the components to match their original state - stored in "op"
     */
    public void resetView() {
        Portfolio temp = data.get(0).getYValue();
        data.clear();
        data.add(new XYChart.Data<>(Calendar.getInstance().getTime(), temp));
        updateView();
    }

    public void updateData() {
        ObservableList<XYChart.Data<Date, Portfolio>> temp = FXCollections.observableArrayList();
        temp.addAll(data);
        data.set(0, new XYChart.Data<>(temp.get(0).getXValue(), op.clone()));
        for(int i = 1; i < temp.size(); i++)
            data.set(i, new XYChart.Data<>(temp.get(i).getXValue(), s.simulate(steps(), step_size(), data.get(i-1).getYValue().clone(), rate())));
        updateView();
    }

    /**
     * Reset the values of the components.
     */
    public void updateView() {
        ObservableList<XYChart.Series<Date, Number>> series = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Date, Number>> series1 = FXCollections.observableArrayList();
        for (XYChart.Data<Date, Portfolio> d : data) {
            XYChart.Data<Date, Number> node = new XYChart.Data<>(d.getXValue(), d.getYValue().eval());
            ClickablePortfolioNode point = new ClickablePortfolioNode(d.getYValue());
            point.setOnSelectedListener(this);
            node.setNode(point);
            series1.add(node);
        }
        series.add(new XYChart.Series<>("Portfolio Value", series1));
        chart.setData(series);
    }

    private void showPortfolioData(Portfolio portfolio) {
        if (portfolio != null)
            port_table.setItems(FXCollections.observableArrayList(portfolio.getHoldings()));
    }

    @Override
    public void handle(ClickablePortfolioNode node) {
        if (selected != null && selected != node)
            selected.deselect();
        selected = node;
        showPortfolioData(node.getPortfolio());
    }

    @Override
    public void update(Observable o, Object arg) {
        updateView();
    }
}

class ClickablePortfolioNode extends StackPane {
    private OnNodeSelectedListener listener;
    private Portfolio port;

    public ClickablePortfolioNode(Portfolio portfolio) {
        final Circle label = new Circle();
        label.setStroke(Color.GREEN);
        label.setFill(Color.LIGHTGREEN);
        label.setRadius(5f);
        this.port = portfolio;

        setOnMouseEntered(mouseEvent -> setCursor(Cursor.HAND));
        setOnMouseExited(mouseEvent -> setCursor(Cursor.CROSSHAIR));
        setOnMouseClicked(mouseEvent -> {
            if(getChildren().size() == 0)
                getChildren().add(label);
            listener.handle(this);
        });
    }

    public Portfolio getPortfolio() {
        return port;
    }

    public void setOnSelectedListener(OnNodeSelectedListener listener) {
        this.listener = listener;
    }

    public void deselect() {
        getChildren().clear();
    }
}

interface OnNodeSelectedListener {
    void handle(ClickablePortfolioNode node);
}