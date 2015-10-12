package main.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import main.FPTS;
import main.controller.Controller;

import java.io.InputStream;
import java.net.URL;
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

    public void setApp(Controller app){
        this.app = app;
    }

    public void initialize(URL location, ResourceBundle resources) {
        app.currentPortfolio.getHoldings();
        gotoEquity();
    }

    public void gotoEquity() {
        try{
            changeScene(getScene("equity.fxml"));
        } catch(Exception e) {
            System.err.println("Error inflating equity view");
        }
    }

    public void gotoAccount() {
        try{
            changeScene(getScene("account.fxml"));
        } catch(Exception e) {
            System.err.println("Error inflating account view");
        }
    }

    public void gotoTransaction() {
        try{
            changeScene(getScene("transaction.fxml"));
        } catch(Exception e) {
            System.err.println("Error inflating transaction view");
        }
    }

    private void changeScene(Scene newScene) {
        if(currentScene != null)
            content.getChildren().removeAll(currentScene.getRoot());
        content.getChildren().add(newScene.getRoot());
        currentScene = newScene;
    }

    private Scene getScene(String fxml) throws Exception {
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
        return new Scene(page);
    }
}
