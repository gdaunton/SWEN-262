package main.view.sub;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleGroup;
import main.model.holdings.Account;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class AccountContoller implements Initializable{

    @FXML
    private TextField name;
    @FXML
    private TextField balance;
    @FXML
    private TextField date;
    @FXML
    private ChoiceBox destination;
    @FXML
    private TextField post;
    @FXML
    private ToggleGroup action;

    private Account account;

    public void setAccount(Account account){
        this.account = account;
        initValues();
    }

    public void initialize(URL location, ResourceBundle resources) {

    }

    private void initValues() {
        name.setText(account.getName());
        balance.setText(Double.toString(account.getBalance()));
    }
}
