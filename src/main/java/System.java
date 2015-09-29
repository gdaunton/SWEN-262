import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Portfolio;

import java.util.ArrayList;

public class System extends Application{

    private ArrayList<Portfolio> portfolios;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent login = FXMLLoader.load(getClass().getResource("login.fxml"));
        Parent create = FXMLLoader.load(getClass().getResource("create_portfolio.fxml"));
        primaryStage.setTitle("FPTS");
        primaryStage.setScene(new Scene(create));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
