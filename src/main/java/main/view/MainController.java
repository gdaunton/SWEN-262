package main.view;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.FPTS;
import main.controller.Controller;
import main.controller.command.HoldingCommand;
<<<<<<< HEAD
=======
import main.model.Portfolio;
>>>>>>> 14c67ff6ed873e378ebeeb45b1ec9fdf5166f5c9
import main.model.Record;
import main.model.holdings.Account;
import main.model.holdings.Equity;
import main.model.holdings.Holding;
import main.view.dialog.DialogController;
import main.view.sub.AccountController;
import main.view.sub.EquityController;
import main.view.sub.TransactionController;

public class MainController implements Initializable {

	private Controller app;

	@FXML
	private Pane content;
	@FXML
	private ListView<Account> account_list;
	@FXML
	private ListView<Equity> equity_list;
	@FXML
	private ListView<Record> record_list;
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

	private Scene currentScene;

	/**
	 * Sets the controller app.
	 * 
	 * @param app
	 *            The app.
	 */
	public void setApp(Controller app) {
		this.app = app;
		updateLists(app.currentPortfolio.getHoldings());
		if (account_list != null && equity_list != null)
			initLists();
		if(app.getOtherPortfolios().size() == 0) {
			open.setDisable(true);
		}
	}

	/**
	 * Initializes the main controller.
	 * 
	 * @param location
	 *            The location.
	 * @param resources
	 *            The resources.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		initMenu();
		if (account_list != null && equity_list != null)
			initLists();
	}

	public void changePortfolio() {
		updateLists(app.currentPortfolio.getHoldings());
	}

	/**
	 * Initializes the menu.
	 */
	private void initMenu() {
		inport.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				showTeamEDialog(false);
			}
		});
		export.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				showTeamEDialog(true);
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
					((main.view.dialog.AccountController) createDialogScene("account.fxml"))
							.setController(MainController.this);
				} catch (Exception e) {
					System.err.println("Error inflating new account dialog");
				}
			}
		});
		equity.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				try {
					((main.view.dialog.EquityController) createDialogScene("equity.fxml"))
							.setController(MainController.this);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Error inflating new equity dialog");
				}
			}
		});
		open.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				ArrayList<Portfolio> temp = app.getOtherPortfolios();
				if(temp.size() != 0) {
					ChoiceDialog<Portfolio> dialog = new ChoiceDialog<Portfolio>(temp.get(0), temp);
					dialog.setTitle("Open Portfolio");
					dialog.setHeaderText("Open a different portfolio...");
					Optional<Portfolio> result = dialog.showAndWait();
					if (result.isPresent()){
						app.setPortfolio(result.get());
					}
				}
			}
		});
	}

	/**
	 * Shows the team export dialog.
	 * 
	 * @param export
	 *            True to export; false otherwise.
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
	 * Initialize the list view.
	 */
	private void initLists() {
		account_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Account>() {
			public void changed(ObservableValue observable, Account oldValue, final Account newValue) {
				if (newValue != null) {
					Platform.runLater(new Runnable() {
						public void run() {
							if(!record_list.getSelectionModel().isEmpty()) {
								record_list.getSelectionModel().clearSelection();
							}
							if (!equity_list.getSelectionModel().isEmpty()) {
								equity_list.getSelectionModel().clearSelection();
							}
							account_list.getSelectionModel().select(newValue);
						}
					});
					gotoAccount(newValue);
				}
			}
		});
		equity_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Equity>() {
			public void changed(ObservableValue observable, Equity oldValue, final Equity newValue) {
				if (newValue != null) {
					Platform.runLater(new Runnable() {
						public void run() {
							if(!record_list.getSelectionModel().isEmpty()) {
								record_list.getSelectionModel().clearSelection();
							}
							if (!account_list.getSelectionModel().isEmpty()) {
								account_list.getSelectionModel().clearSelection();
							}
							equity_list.getSelectionModel().select(newValue);
						}
					});
					gotoEquity(newValue);
				}
			}
		});
	}

	/**
	 * Sends a command.
	 * 
	 * @param type
	 *            The type of the command.
	 * @param holding
	 *            The holding to act on.
	 */
	public void sendCommand(HoldingCommand.Action type, Holding holding) {
		app.executeCommand(new HoldingCommand(type, app.currentPortfolio, holding));
	}

	/**
	 * Sends a command.
	 * 
	 * @param type
	 *            The type of the command.
	 * @param holding
	 *            The holding to act on.
	 * @param modification
	 *            The action to take.
	 * @param modifier
	 *            The type of holding.
	 */
	public void sendCommand(HoldingCommand.Action type, Holding holding, HoldingCommand.Modification modification,
			double modifier) {
		app.executeCommand(new HoldingCommand(type, app.currentPortfolio, holding, modification, modifier));
	}

	/**
	 * Sends a command.
	 * 
	 * @param type
	 *            The type of the command.
	 * @param origin
	 *            The origin of the command.
	 * @param destination
	 *            THe destination of the command.
	 * @param modification
	 *            The action to take on the holding.
	 * @param modifier
	 *            The type of holding.
	 */
	public void sendCommand(HoldingCommand.Action type, Account origin, Account destination,
			HoldingCommand.Modification modification, double modifier) {
		app.executeCommand(new HoldingCommand(type, app.currentPortfolio, origin, destination, modification, modifier));
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
		return new ArrayList<Account>(this.account_list.getItems());
	}

	/**
	 * Updates the displayed information.
	 */
	public void update() {
		updateLists(app.currentPortfolio.getHoldings());
		if (!account_list.getSelectionModel().isEmpty()) {
			gotoAccount(account_list.getSelectionModel().getSelectedItem());
		}
		else if (!equity_list.getSelectionModel().isEmpty()) {
			gotoEquity(equity_list.getSelectionModel().getSelectedItem());
		}
		record_list.setItems(new FXCollections.observableArrayList(app.currentPortfolio.history));
	}

	/**
	 * Updates the holdings list.
	 * 
	 * @param holdings
	 *            The holdings to update.
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
			account_list.setItems(accountItems);
			equity_list.setItems(equityItems);
		}
	}

	/**
	 * Go to an equity.
	 * 
	 * @param equity
	 *            The equity to go to.
	 */
	public void gotoEquity(Equity equity) {
		try {
			EquityController e = (EquityController) changeScene("equity.fxml");
			e.setEquity(this, equity);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error inflating equity view");
		}
	}

	/**
	 * Go to an account.
	 * 
	 * @param account
	 *            The account to go to.
	 */
	public void gotoAccount(Account account) {
		try {
			AccountController a = (AccountController) changeScene("account.fxml");
			a.setAccount(this, account);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error inflating account view");
		}
	}

	/**
	 * Go to a transaction.
	 */
	public void gotoTransaction(Record record) {
		try {
			TransactionController t = (TransactionController) changeScene("transaction.fxml");
			t.setTransaction(this, record);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error inflating transaction view");
		}
	}

	/**
	 * Initializes a dialog.
	 * 
	 * @param fxml
	 *            The FXML string.
	 * @return An Initializable object.
	 * @throws Exception
	 *             If an error occurs.
	 */
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
		((DialogController) loader.getController()).setStage(s);
		return loader.getController();
	}

	/**
	 * Changes a scene.
	 * 
	 * @param fxml
	 *            The FXML string.
	 * @return An Initializable object.
	 * @throws Exception
	 *             If an error occurs.
	 */
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
		if (currentScene != null)
			content.getChildren().removeAll(currentScene.getRoot());
		content.getChildren().add(newScene.getRoot());
		currentScene = newScene;
		return loader.getController();
	}
}
