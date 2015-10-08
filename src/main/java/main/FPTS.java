package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.model.Portfolio;
import main.model.User;
import main.model.UserManager;
import main.view.LoginController;
import main.view.MainController;
import main.view.PortfolioCreateController;
import main.view.UserCreateController;

import java.io.InputStream;
import java.util.ArrayList;

public class FPTS extends Application{

    private ArrayList<Portfolio> portfolios;
    private Stage stage;
    private UserManager um;
    private User loggedUser;

    @Override
    public void start(Stage primaryStage) throws Exception{
        um = new UserManager();
        try {
            stage = primaryStage;
            gotoLogin();
            primaryStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public boolean handleLogin(String userId, String password) throws UserManager.InvalidPasswordException, Exception{
        loggedUser = um.checkUser(userId, password);
        if (loggedUser == null)
            return false;
        else {
            gotoMain();
            return true;
        }
    }

    public boolean createUser(String userId, String password) throws UserManager.UsernameOccupiedException, Exception{
        loggedUser = um.createUser(userId, password);
        if (loggedUser == null)
            return false;
        else {
            gotoMain();
            return true;
        }
    }

    public void gotoLogin() {
        try {
            LoginController l = (LoginController)replaceSceneContent("login.fxml");
            l.setApp(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void gotoCreateUser() {
        try {
            UserCreateController l = (UserCreateController)replaceSceneContent("create_user.fxml");
            l.setApp(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void gotoCreatePortfolio () {
        try {
            PortfolioCreateController m = (PortfolioCreateController)replaceSceneContent("create_portfolio.fxml");
            m.setApp(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void gotoMain() {
        try {
            MainController m = (MainController)replaceSceneContent("main.fxml");
            m.setApp(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Initializable replaceSceneContent(String fxml) throws Exception {
        fxml = "/" + fxml;
        FXMLLoader loader = new FXMLLoader();
        InputStream in = getClass().getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(getClass().getResource(fxml));
        Pane page;
        try {
            page = (Pane) loader.load(in);
        } finally {
            in.close();
        }
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }
}
