package main.view;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import main.FPTS;
import main.controller.Controller;
import main.controller.command.Command;
import main.controller.command.HoldingCommand;
import main.model.Portfolio;
import main.model.holdings.*;
import main.model.simulation.Bear;
import main.model.simulation.Bull;
import main.model.simulation.NoGrowth;
import main.model.simulation.Simulation;
import main.model.user.User;
import main.view.dialog.DialogController;
import main.view.elements.IntegerTextField;
import main.view.simulation.SimulationController;
import main.view.sub.AccountController;
import main.view.sub.EquityController;
import main.view.sub.TransactionController;
import main.view.sub.WatchedEquityController;

public class MainController extends Observable implements Initializable{

    private Controller app;

    @FXML
    private Pane content;
    @FXML
    private ListView<Account> account_list;
    @FXML
    private ListView<Equity> equity_list;
    @FXML
    private ListView<WatchedEquity> watchlist;
    @FXML
    private Button watch_remove;
    @FXML
    private Button watch_add;
    @FXML
    private MenuItem inport;
    @FXML
    private MenuItem export;
    @FXML
    private MenuItem bear;
    @FXML
    private MenuItem bull;
    @FXML
    private MenuItem no_grow;
    @FXML
    private MenuItem account;
    @FXML
    private MenuItem equity;
    @FXML
    private MenuItem open;
    @FXML
    private MenuItem transactions;
    @FXML
    private MenuItem undo;
    @FXML
    private MenuItem redo;
    @FXML
    private MenuItem interval;
    @FXML
    private MenuItem logout;
    @FXML
    private Text dow;

    private Scene currentScene;

    /**
     * Sets the controller app.
     *
     * @param app The app.
     */
    public void setApp(Controller app) {
        this.app = app;
        if (account_list != null && equity_list != null)
            initLists();
        if (app.getOtherPortfolios().size() == 0) {
            open.setDisable(true);
        }
        update();
    }

    /**
     * Initializes the main controller.
     *
     * @param location  The location.
     * @param resources The resources.
     */
    public void initialize(URL location, ResourceBundle resources) {
        initMenu();
        if (account_list != null && equity_list != null)
            initLists();

        watch_add.setOnAction(event -> {
            try {
                ((main.view.dialog.WatchEquityController) createDialogScene("watched.fxml"))
                        .setController(MainController.this);
            } catch (Exception e) {
                System.err.println("Error inflating watchlist dialog");
                e.printStackTrace();
            }
        });
        watch_remove.setOnAction(event -> {
            WatchedEquity remove = watchlist.getSelectionModel().getSelectedItem();
            getUser().watchedEquities.remove(remove);
            update();
        });
    }

    public void changePortfolio() {
        updateLists(app.currentPortfolio.getHoldings());
    }

    /**
     * Initializes the menu.
     */
    private void initMenu() {
        open.setOnAction(event -> {
            ArrayList<Portfolio> temp = app.getOtherPortfolios();
            if (temp.size() != 0) {
                ChoiceDialog<Portfolio> dialog = new ChoiceDialog<>(temp.get(0), temp);
                dialog.setTitle("Open Portfolio");
                dialog.setHeaderText("Open a different portfolio...");
                Optional<Portfolio> result = dialog.showAndWait();
                if (result.isPresent()) {
                    app.setPortfolio(result.get());
                }
            }
            app.update();
        });
        transactions.setOnAction(event -> gotoTransaction());
        inport.setOnAction(event -> showTeamEDialog(false));
        export.setOnAction(event -> showTeamEDialog(true));
        bear.setOnAction(event -> {
            openSimulation(new Bear());
        });
        bull.setOnAction(event -> {
            openSimulation(new Bull());
        });
        no_grow.setOnAction(event -> {
            openSimulation(new NoGrowth());
        });
        account.setOnAction(event -> {
            try {
                ((main.view.dialog.AccountController) createDialogScene("account.fxml"))
                        .setController(MainController.this);
            } catch (Exception e) {
                System.err.println("Error inflating new account dialog");
            }
            app.update();
        });
        equity.setOnAction(event -> {
            try {
                ((main.view.dialog.EquityController) createDialogScene("equity.fxml"))
                        .setController(MainController.this);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error inflating new equity dialog");
            }
            app.update();
        });
        undo.setOnAction(event -> app.undo());
        redo.setOnAction(event -> app.redo());
        interval.setOnAction(event -> showIntervalDialog());
        logout.setOnAction(event -> app.logout());
    }

    /**
     * Shows the team export dialog.
     *
     * @param export True to export; false otherwise.
     */
    private void showTeamEDialog(boolean export) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choose export format");
        ButtonType e = new ButtonType("Team E's format");
        ButtonType no = new ButtonType("Team D's format");
        alert.getButtonTypes().setAll(e, no);
        Optional<ButtonType> result = alert.showAndWait();

        FileChooser.ExtensionFilter csv = new FileChooser.ExtensionFilter("csv", "*.csv");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(csv);

        File file;
        if (export) {
            fileChooser.setTitle("Export Portfolio");
            file = fileChooser.showSaveDialog(content.getScene().getWindow());
        } else {
            fileChooser.setTitle("Import Portfolio");
            file = fileChooser.showOpenDialog(content.getScene().getWindow());
        }
        if (result.get() == e) {
            if (export)
                app.currentPortfolio.export_holdings(file, false);
            else
                app.currentPortfolio.import_holdings(file, false);
        } else {
            if (export)
                app.currentPortfolio.export_holdings(file);
            else
                app.currentPortfolio.import_holdings(file);
        }
    }

    /**
     * Show the dialog to change the update interval
     */
    private void showIntervalDialog() {
        Dialog<Integer> dialog = new Dialog<>();
        ButtonType setButtonType = new ButtonType("Set", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(setButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        IntegerTextField interval = new IntegerTextField();
        interval.setText(Integer.toString(app.getPollerRefreshRate()));
        grid.add(new Label("Interval (Sec):"), 0, 0);
        grid.add(interval, 1, 0);

        Node setButton = dialog.getDialogPane().lookupButton(setButtonType);
        setButton.setDisable(true);

        interval.textProperty().addListener((observable, oldValue, newValue) -> {
            setButton.setDisable(newValue.trim().isEmpty());
        });
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(interval::requestFocus);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == setButtonType)
                return interval.getInteger();
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();
        if(result.isPresent())
            app.setPollerRefreshRate(result.get());
    }

    /**
     * Initialize the list view.
     */
    private void initLists() {
        account_list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Platform.runLater(() -> {
                    if (!equity_list.getSelectionModel().isEmpty())
                        equity_list.getSelectionModel().clearSelection();
                    if(!watchlist.getSelectionModel().isEmpty())
                        watchlist.getSelectionModel().clearSelection();
                    account_list.getSelectionModel().select(newValue);
                });
                gotoAccount(newValue);
            }
        });
        equity_list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Platform.runLater(() -> {
                    if (!account_list.getSelectionModel().isEmpty())
                        account_list.getSelectionModel().clearSelection();
                    if(!watchlist.getSelectionModel().isEmpty())
                        watchlist.getSelectionModel().clearSelection();
                    equity_list.getSelectionModel().select(newValue);
                });
                gotoEquity(newValue);
            }
        });
        watchlist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Platform.runLater(() -> {
                    if (!account_list.getSelectionModel().isEmpty())
                        account_list.getSelectionModel().clearSelection();
                    if (!equity_list.getSelectionModel().isEmpty())
                        equity_list.getSelectionModel().clearSelection();
                    watchlist.getSelectionModel().select(newValue);
                });
                gotoWatchedEquity(newValue);
            }
        });
    }

    /**
     * Sends a command.
     *
     * @param type    The type of the command.
     * @param holding The holding to act on.
     */
    public void sendCommand(HoldingCommand.Action type, Holding holding) {
        app.executeCommand(new HoldingCommand(type, app.currentPortfolio, holding));
    }

    /**
     * Sends a command.
     *
     * @param type         The type of the command.
     * @param holding      The holding to act on.
     * @param modification The action to take.
     * @param modifier     The type of holding.
     */
    public void sendCommand(HoldingCommand.Action type, Holding holding, HoldingCommand.Modification modification, double modifier) {
        app.executeCommand(new HoldingCommand(type, app.currentPortfolio, holding, modification, modifier));
    }

    /**
     * Sends a command.
     *
     * @param type         The type of the command.
     * @param origin       The origin of the command.
     * @param destination  THe destination of the command.
     * @param modification The action to take on the holding.
     * @param modifier     The type of holding.
     */
    public void sendCommand(HoldingCommand.Action type, Account origin, Account destination, HoldingCommand.Modification modification, double modifier) {
        app.executeCommand(new HoldingCommand(type, app.currentPortfolio, origin, destination, modification, modifier));
    }

    /**
     * Sends a command.
     *
     * @param command the command to send
     */
    public void sendCommand(Command command) {
        app.executeCommand(command);
    }

    public Portfolio getPortfolio() {
        return app.currentPortfolio;
    }

    /**
     * Get the currently selected holding.
     *
     * @return The selected holding or null if nothing is selected
     */
    public Holding getSelectedItem() {
        if (!equity_list.getSelectionModel().isEmpty())
            return equity_list.getSelectionModel().getSelectedItem();
        if (!account_list.getSelectionModel().isEmpty())
            return account_list.getSelectionModel().getSelectedItem();
        return null;
    }

    /**
     * Gets the accounts.
     *
     * @return The list of accounts.
     */
    public ArrayList<Account> getAccounts() {
        return new ArrayList<>(this.account_list.getItems());
    }

    /**
     * Get the user that is currently logged in.
     *
     * @return The user that is currently logged in.
     */
    public User getUser() {
        return app.getUser();
    }

    /**
     * Updates the displayed information.
     */
    public void update() {
        updateLists(app.currentPortfolio.getHoldings());
        if (!account_list.getSelectionModel().isEmpty()) {
            gotoAccount(account_list.getSelectionModel().getSelectedItem());
        } else if (!equity_list.getSelectionModel().isEmpty()) {
            gotoEquity(equity_list.getSelectionModel().getSelectedItem());
        } else if (!watchlist.getSelectionModel().isEmpty()) {
            gotoWatchedEquity(watchlist.getSelectionModel().getSelectedItem());
        } else {
            gotoTransaction();
        }
        undo.setDisable(!app.canUndo());
        redo.setDisable(!app.canRedo());
        dow.setText(Double.toString(HoldingManager.djia()));
        notifyObservers();
        setChanged();
    }

    /**
     * Updates the holdings list.
     *
     * @param holdings The holdings to update.
     */
    private void updateLists(ArrayList<Holding> holdings) {
        ObservableList<Account> accountItems = FXCollections.observableArrayList();
        ObservableList<Equity> equityItems = FXCollections.observableArrayList();
        if (holdings != null) {
            for (Holding h : holdings) {
                if (h instanceof Account) {
                    accountItems.add((Account) h);
                } else if (h instanceof Equity) {
                    equityItems.add((Equity) h);
                }
            }
            watchlist.setItems(FXCollections.observableArrayList(getUser().watchedEquities));
            account_list.setItems(accountItems);
            equity_list.setItems(equityItems);
        }
    }

    /**
     * Open the simulation dialog
     * @param simulation the simulation to attach to the dialog
     */
    public void openSimulation(Simulation simulation) {
        if(app.currentPortfolio.getHoldings().size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("UtOh!");
            alert.setHeaderText(null);
            alert.setContentText("I can't simulate a portfolio without any holdings!!");
            alert.showAndWait();
        }else {
            try {
                createSimulationScene("simulation.fxml").setSimulation(this, simulation);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error inflating simulation view");
            }
        }
    }
    /**
     * Go to an equity.
     *
     * @param equity The equity to go to.
     */
    public void gotoEquity(Equity equity) {
        try {
            EquityController e = (EquityController) changeScene("sub/equity.fxml");
            e.setEquity(this, equity);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error inflating equity view");
        }
    }

    /**
     * Goto the watched equity
     * @param watchedEquity show the watched equity
     */
    private void gotoWatchedEquity(WatchedEquity watchedEquity) {
        try {
            WatchedEquityController e = (WatchedEquityController) changeScene("sub/watched.fxml");
            e.setWatchedEquity(watchedEquity);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error inflating watched equity view");
        }
    }

    /**
     * Go to an account.
     *
     * @param account The account to go to.
     */
    public void gotoAccount(Account account) {
        try {
            AccountController a = (AccountController) changeScene("sub/account.fxml");
            a.setAccount(this, account);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error inflating account view");
        }
    }

    /**
     * Go to a transaction.
     */
    public void gotoTransaction() {
        try {
            TransactionController t = (TransactionController) changeScene("sub/transaction.fxml");
            t.setTransaction(this, app.currentPortfolio.history);
            account_list.getSelectionModel().clearSelection();
            equity_list.getSelectionModel().clearSelection();
            watchlist.getSelectionModel().clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error inflating transaction view");
        }
    }

    /**
     * Initializes a dialog.
     *
     * @param fxml The FXML string.
     * @return An Initializable object.
     * @throws Exception If an error occurs.
     */
    private Initializable createDialogScene(String fxml) throws Exception {
        fxml = "/dialog/" + fxml;
        Stage s = new Stage();
        s.initStyle(StageStyle.UTILITY);
        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(FPTS.class.getResource(fxml));
        Pane page;
        try (InputStream in = getClass().getResourceAsStream(fxml)) {
            page = loader.load(in);
        }
        Scene newScene = new Scene(page);
        s.setScene(newScene);
        s.show();
        ((DialogController) loader.getController()).setStage(s);
        return loader.getController();
    }

    /**
     * Initializes a Simulation Window.
     *
     * @param fxml The FXML string.
     * @return An Initializable object.
     * @throws Exception If an error occurs.
     */
    private SimulationController createSimulationScene(String fxml) throws Exception {
        fxml = "/simulation/" + fxml;
        Stage s = new Stage();
        s.initStyle(StageStyle.UTILITY);
        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(FPTS.class.getResource(fxml));
        Pane page;
        try (InputStream in = getClass().getResourceAsStream(fxml)) {
            page = loader.load(in);
        }
        Scene newScene = new Scene(page);
        s.setScene(newScene);
        s.show();
        return loader.getController();
    }

    /**
     * Changes a scene.
     *
     * @param fxml The FXML string.
     * @return An Initializable object.
     * @throws Exception If an error occurs.
     */
    private Initializable changeScene(String fxml) throws Exception {
        fxml = "/" + fxml;
        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(FPTS.class.getResource(fxml));
        Pane page;
        try (InputStream in = getClass().getResourceAsStream(fxml)) {
            page = loader.load(in);
        }
        Scene newScene = new Scene(page);
        if (currentScene != null)
            content.getChildren().removeAll(currentScene.getRoot());
        content.getChildren().add(newScene.getRoot());
        currentScene = newScene;
        return loader.getController();
    }
}