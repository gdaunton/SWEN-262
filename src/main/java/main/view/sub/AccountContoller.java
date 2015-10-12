package main.view.sub;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import main.model.holdings.Account;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountContoller implements Initializable{

    @FXML
    private Label name;

    private Account account;

    public void setAccount(Account account){
        this.account = account;
    }

    public void initialize(URL location, ResourceBundle resources) {

    }
}
