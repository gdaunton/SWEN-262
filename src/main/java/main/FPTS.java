package main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.controller.Controller;
import main.model.Portfolio;
import main.model.User;
import main.model.UserManager;
import main.model.util.HashSlingingSlasher;
import main.view.LoginController;
import main.view.MainController;
import main.view.PortfolioCreateController;
import main.view.UserCreateController;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Optional;

public class FPTS extends Application{

    private ArrayList<Portfolio> portfolios;
    private PortfolioManager manager;
    private Stage stage;
    private UserManager um;
    private User loggedUser;
    private String dataRoot = "data/";

    @Override
    public void start(Stage primaryStage) throws Exception{
        new File(dataRoot).mkdir();
        um = new UserManager(dataRoot);
        try {
            stage = primaryStage;
            gotoLogin();
            primaryStage.show();
            manager = new PortfolioManager(new File(dataRoot + "portfolios.dat"));
            portfolios = manager.getPortfolios();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    if(!checkDataChanged())
                        we.consume();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Checks for if any data was changed by the user.
     * @return true if event is handled, false if not.
     */
    private boolean checkDataChanged() {
        boolean changed = false;
        ArrayList<Portfolio> temp = manager.getPortfolios();
        if (temp.size() == portfolios.size()) {
            for (int i = 0; i < temp.size(); i++) {
                if (!temp.get(i).equals(portfolios.get(i))) {
                    changed = true;
                    break;
                }
            }
        } else
            changed = true;
        if(changed) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved changes!");
            alert.setContentText("Would you like to exit without saving?");
            ButtonType save = new ButtonType("Save");
            ButtonType dontSave = new ButtonType("Don't Save");
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(save, dontSave, cancel);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == save) {
                manager.savePortfolios(portfolios);
            } else if (result.get() == dontSave) {
                //do nothing
            } else {
                return false;
            }
            return true;
        }
        return true;
    }

    private void initPortfolio(User user){
        Controller c = new Controller(portfolios, user, gotoMain());
        c.setOnLogout(new Controller.OnLogout() {
            public void Logout() {
                if(checkDataChanged())
                    System.exit(0);
            }
        });
    }

    public void createPortfolio(Portfolio portfolio){
        this.portfolios.add(portfolio);
    }

    public boolean handleLogin(String userId, String password) throws UserManager.InvalidPasswordException, Exception{
        loggedUser = um.checkUser(userId, password);
        if (loggedUser == null)
            return false;
        else {
            initPortfolio(loggedUser);
            return true;
        }
    }

    public boolean createUser(String userId, String password) throws UserManager.UsernameOccupiedException, Exception{
        loggedUser = um.createUser(userId, password);
        if (loggedUser == null)
            return false;
        else {
            initPortfolio(loggedUser);
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

    private MainController gotoMain() {
        try {
            MainController m = (MainController)replaceSceneContent("main.fxml");
            m.setApp(this);
            return m;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
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

    public class PortfolioManager {
        private File portfolioFile;

        public PortfolioManager(File portfolioFile){
            this.portfolioFile = portfolioFile;
            try {
                if (!portfolioFile.exists())
                    portfolioFile.createNewFile();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        public ArrayList<Portfolio> getPortfolios(){
            ArrayList<Portfolio> out = new ArrayList<Portfolio>();
            try {
                Portfolio current;
                InputStream buffer = new BufferedInputStream(new FileInputStream(portfolioFile));
                ObjectInputStream s = new ObjectInputStream(buffer);
                while (buffer.available() > 0 && (current = (Portfolio) s.readObject()) != null)
                    out.add(current);
                buffer.close();
                s.close();
            } catch (EOFException e) {
                return out;
            } catch (Exception e){
                System.err.println("Error retrieving portfolios from file");
            }
            return out;
        }

        public void savePortfolios(ArrayList<Portfolio> portfolios){
            if(portfolios != null){
                try{
                    OutputStream buffer = new BufferedOutputStream(new FileOutputStream(portfolioFile, false));
                    ObjectOutputStream stream = new ObjectOutputStream(buffer);
                    for(Portfolio p : portfolios)
                        stream.writeObject(p);
                    buffer.close();
                    stream.close();
                } catch(Exception e){
                    System.err.println("Error saving portfolios to file");
                    e.printStackTrace();
                }
            }
        }
    }
}
