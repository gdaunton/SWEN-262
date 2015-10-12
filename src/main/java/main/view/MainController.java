package main.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import main.FPTS;
import main.controller.Command;
import main.controller.Controller;
import main.controller.command.HoldingCommand;
import main.model.holdings.Account;
import main.model.holdings.Equity;
import main.model.holdings.Holding;
import main.view.sub.AccountContoller;
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
    private ListView account_list;
    @FXML
    private ListView equity_list;

    private Scene currentScene;
    private ArrayList<Account> accounts;
    private ArrayList<Equity> equities;

    public void setApp(Controller app){
        this.app = app;
    }

    public void initialize(URL location, ResourceBundle resources) {
        accounts = new ArrayList<Account>();
        equities = new ArrayList<Equity>();
        updateLists(app.currentPortfolio.getHoldings());

        account_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                equity_list.getSelectionModel().clearSelection();
                gotoAccount(accounts.get(account_list.getSelectionModel().getSelectedIndex()));
            }
        });
        equity_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                account_list.getSelectionModel().clearSelection();
                gotoEquity(equities.get(equity_list.getSelectionModel().getSelectedIndex()));
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
            return equities.get(equity_list.getSelectionModel().getSelectedIndex());
        if(!account_list.getSelectionModel().isEmpty())
            return accounts.get(account_list.getSelectionModel().getSelectedIndex());
        return null;
    }

    public ArrayList<Account> getAccounts(){
        return this.accounts;
    }

    private void updateLists(ArrayList<Holding> holdings) {
        accounts.removeAll(accounts);
        equities.removeAll(equities);
        ObservableList<String> accountItems = FXCollections.observableArrayList();
        ObservableList<String> equityItems = FXCollections.observableArrayList();
        for(Holding h : holdings) {
            try {
                Account a = (Account)h;
                accountItems.add(a.getName());
                accounts.add(a);
            } catch (ClassCastException e){
                try {
                    Equity eq = (Equity)h;
                    equityItems.add(eq.getName());
                    equities.add(eq);
                } catch (ClassCastException e1) {
                    System.err.println("Error sorting out accounts and equities from holdings");
                }
            } finally {
                account_list.setItems(accountItems);
                equity_list.setItems(equityItems);
            }
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
            AccountContoller a = (AccountContoller)changeScene("account.fxml");
            a.setAccount(account);
        } catch(Exception e) {
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
