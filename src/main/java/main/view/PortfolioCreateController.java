package main.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import main.FPTS;
import main.model.Portfolio;
import main.model.User;
import main.model.UserManager;

public class PortfolioCreateController implements Initializable {

    @FXML
    private TextField name;
    @FXML
    private Button importButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button createButton;
    @FXML
    private Text errorText;
    @FXML
    private ListView users;

    private FPTS app;

    /**
     * Sets the FPTS app.
     *
     * @param app The app.
     */
    public void setApp(FPTS app) {
        this.app = app;
    }

    /**
     * Initializes the portfolio creator.
     *
     * @param location  The location.
     * @param resources The resources.
     */
    public void initialize(URL location, ResourceBundle resources) {
        users.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        final ArrayList<User> allUsers = UserManager.getAllUsers();
        ObservableList<String> userList = FXCollections.observableArrayList();
        for (User u : allUsers)
            userList.add(u.username);
        users.setItems(userList);

        assert importButton != null : "fx:id=\"importButton\" was not injected: check your FXML file.";
        importButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                // TODO Do file selection and then pass to csv importer
            }
        });

        assert createButton != null : "fx:id=\"createButton\" was not injected: check your FXML file.";
        createButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                ArrayList<User> temp = new ArrayList<User>();
                for (Object i : users.getSelectionModel().getSelectedIndices()) {
                    int in = (Integer) i;
                    temp.add(allUsers.get(in));
                }
                if (!name.getText().equals("")) {
                    app.createPortfolio(new Portfolio(temp, name.getText()));
                    app.gotoLogin();
                } else {
                    errorText.setText("Please input a name for the portfolio");
                    errorText.setVisible(true);
                }
            }
        });

        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file.";
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                app.gotoLogin();
            }
        });
    }
}
