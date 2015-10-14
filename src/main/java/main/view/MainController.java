package main.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.FPTS;
import main.controller.Controller;
import main.controller.command.HoldingCommand;
import main.model.holdings.Account;
import main.model.holdings.Equity;
import main.model.holdings.Holding;
import main.view.dialog.DialogController;
import main.view.sub.AccountController;
import main.view.sub.EquityController;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private Controller app;

    @FXML
    private Pane content;
    @FXML
    private ListView<Account> account_list;
    @FXML
    private ListView<Equity> equity_list;
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


    private Scene currentScene;

    public void setApp(Controller app){
        this.app = app;
        updateLists(app.currentPortfolio.getHoldings());
        if(account_list != null && equity_list != null)
            initLists();
    }

    public void initialize(URL location, ResourceBundle resources) {
        initMenu();
        if(account_list != null && equity_list != null)
            initLists();
    }

    private void initMenu() {
        inport.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

            }
        });
        export.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

            }
        });
        bear.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

            }
        });
        bull.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

            }
        });
        no_grow.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

            }
        });
        account.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    ((main.view.dialog.AccountController) createDialogScene("account.fxml")).setController(MainController.this);
                } catch (Exception e) {
                    System.err.println("Error inflating new account dialog");
                }
            }
        });
        equity.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    ((main.view.dialog.EquityController) createDialogScene("equity.fxml")).setController(MainController.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error inflating new equity dialog");
                }
            }
        });
    }

    private void initLists() {
        account_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                equity_list.getSelectionModel().clearSelection();
                gotoAccount(account_list.getSelectionModel().getSelectedItem());
            }
        });
        equity_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                account_list.getSelectionModel().clearSelection();
                gotoEquity(equity_list.getSelectionModel().getSelectedItem());
            }
        });
    }

    public void sendCommand(HoldingCommand.Action type, Holding holding) {
        app.executeCommand(new HoldingCommand(type, app.currentPortfolio, holding));
    }

    public void sendCommand(HoldingCommand.Action type, Holding holding, HoldingCommand.Modification modification, double modifier) {
        app.executeCommand(new HoldingCommand(type, app.currentPortfolio, holding, modification, modifier));
    }

    public void sendCommand(HoldingCommand.Action type, Account origin, Account destination, HoldingCommand.Modification modification, double modifier) {
        app.executeCommand(new HoldingCommand(type, app.currentPortfolio, origin, destination, modification, modifier));
    }

    /**
     * Get the currently selected holding.
     * @return The selected holding or null if nothing is selected
     */
    public Holding getSelectedItem(){
        if(!equity_list.getSelectionModel().isEmpty())
            return equity_list.getSelectionModel().getSelectedItem();
        if(!account_list.getSelectionModel().isEmpty())
            return account_list.getSelectionModel().getSelectedItem();
        return null;
    }

    public ArrayList<Account> getAccounts(){
        return new ArrayList<Account>(this.account_list.getItems());
    }

    public void update() {
        updateLists(app.currentPortfolio.getHoldings());
        if(!account_list.getSelectionModel().isEmpty())
            gotoAccount(account_list.getSelectionModel().getSelectedItem());
        else if(!equity_list.getSelectionModel().isEmpty())
            gotoAccount(account_list.getSelectionModel().getSelectedItem());
    }

    private void updateLists(ArrayList<Holding> holdings) {
        ObservableList<Account> accountItems = FXCollections.observableArrayList();
        ObservableList<Equity> equityItems = FXCollections.observableArrayList();
        if(holdings != null) {
            for (Holding h : holdings) {
                if(h instanceof Account){
                    accountItems.add((Account) h);
                } else if(h instanceof Equity) {
                    equityItems.add((Equity) h);
                }
            }
            account_list.setItems(accountItems);
            equity_list.setItems(equityItems);
        }
    }

    public void gotoEquity(Equity equity) {
        try{
            EquityController e = (EquityController)changeScene("equity.fxml");
            e.setEquity(this, equity);
        } catch(Exception e) {
            System.err.println("Error inflating equity view");
        }
    }

    public void gotoAccount(Account account) {
        try{
            AccountController a = (AccountController)changeScene("account.fxml");
            a.setAccount(this, account);
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Error inflating account view");
        }
    }

    public void gotoTransaction() {
        try{
            changeScene("transaction.fxml");
        } catch(Exception e) {
            System.err.println("Error inflating transaction view");
        }
    }

    private Initializable createDialogScene(String fxml) throws Exception {
        fxml = "/dialog/" + fxml;
        Stage s = new Stage();
        s.initStyle(StageStyle.UTILITY);
        FXMLLoader loader = new FXMLLoader();
        InputStream in = getClass().getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(FPTS.class.getResource(fxml));
        Pane page;
        try {
            page = (Pane) loader.load(in);
        } finally {
            in.close();
        }
        Scene newScene = new Scene(page);
        s.setScene(newScene);
        s.show();
        ((DialogController)loader.getController()).setStage(s);
        return loader.getController();
    }
    private Initializable changeScene(String fxml) throws Exception {
        fxml = "/main/" + fxml;
        FXMLLoader loader = new FXMLLoader();
        InputStream in = getClass().getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(FPTS.class.getResource(fxml));
        Pane page;
        try {
            page = (Pane) loader.load(in);
        } finally {
            in.close();
        }
        Scene newScene = new Scene(page);
        if(currentScene != null)
            content.getChildren().removeAll(currentScene.getRoot());
        content.getChildren().add(newScene.getRoot());
        currentScene = newScene;
        return loader.getController();
    }
}
