package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.controller.Controller;
import main.model.Portfolio;
import main.model.holdings.HoldingManager;
import main.model.user.User;
import main.model.user.UserManager;
import main.view.MainController;
import main.view.startup.LoginController;
import main.view.startup.PortfolioCreateController;
import main.view.startup.UserCreateController;

public class FPTS extends Application {

	private static boolean file_basis = false;

	private ArrayList<Portfolio> portfolios;
	private PortfolioManager manager;
	private Stage stage;
	private UserManager um;
	private User loggedUser;
	private String dataRoot = "data/";

	/**
	 * Starts the stage.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		new File(dataRoot).mkdir();
		um = new UserManager(dataRoot);

		try {
			if (getParameters().getRaw().get(0).equals("-delete"))
				if (!deleteUser(getParameters().getRaw().get(1)))
					System.out.println("User does not exist!");
		} catch (IndexOutOfBoundsException e) {
		}

		if (file_basis) { // import the equities from the file
			File equities = new File(dataRoot + "equities.csv");
			if (!equities.exists()) {
				FileChooser.ExtensionFilter csv = new FileChooser.ExtensionFilter("csv", "*.csv");
				FileChooser fileChooser = new FileChooser();
				fileChooser.getExtensionFilters().add(csv);
				fileChooser.setTitle("Select Equities File");
				File file = fileChooser.showOpenDialog(stage);
				equities.createNewFile();
				copyFile(file, equities);
			}
			HoldingManager.import_equities(equities);
		} else
			HoldingManager.import_equities_yahoo();

		try {
			stage = primaryStage;
			gotoLogin();
			primaryStage.show();
			manager = new PortfolioManager(new File(dataRoot + "portfolios.dat"));
			portfolios = manager.getPortfolios();
			for (Portfolio p : portfolios) {
				HoldingManager.link_holdings(p);
			}
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					um.logoout(loggedUser);
					if (!checkDataChanged())
						we.consume();
					else
						System.exit(0);
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Copies a file to another.
	 *
	 * @param source
	 *            The source file.
	 * @param dest
	 *            The destination file.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public static void copyFile(File source, File dest) throws IOException {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(source).getChannel();
			outputChannel = new FileOutputStream(dest).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			inputChannel.close();
			outputChannel.close();
		}
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            This application takes no arguments.
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Checks for if any data was changed by the user.
	 *
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
		if (changed) {
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
				// do nothing
			} else {
				return false;
			}
			return true;
		}
		return true;
	}

	/**
	 * Initialize an instance of controller with the given user.
	 *
	 * @param user
	 *            The user to initialize the controller with.
	 */
	private void initPortfolio(User user) {
		Controller c = new Controller(portfolios, user, gotoMain(), stage);
		c.setOnLogout(() -> {
            if (checkDataChanged())
                gotoLogin();
            um.logoout(loggedUser);
        });
	}

	/**
	 * Add a new portfolio object to the system.
	 *
	 * @param portfolio
	 *            The portfolio to add.
	 */
	public void createPortfolio(Portfolio portfolio) {
		this.portfolios.add(portfolio);
	}

	/**
	 * Handle user login.
	 *
	 * @param userId
	 *            The user's username
	 * @param password
	 *            The user's password
	 * @return if the login was successful
	 * @throws UserManager.InvalidPasswordException
	 * @throws Exception
	 */
	public boolean handleLogin(String userId, String password)
			throws UnassociatedUserException, UserManager.InvalidPasswordException {
		loggedUser = um.checkUser(userId, password);
		if (loggedUser == null)
			return false;
		else {
			for (Portfolio p : portfolios) {
				if (p.getUsers().contains(loggedUser)) {
					initPortfolio(loggedUser);
					return true;
				}
			}
			throw new UnassociatedUserException("This user isn't associated with a portfolio");
		}
	}

	/**
	 * Create a new user
	 *
	 * @param userId
	 *            The user's username
	 * @param password
	 *            The user's password
	 * @return if the user was successfully created
	 * @throws UserManager.UsernameOccupiedException
	 * @throws Exception
	 */
	public boolean createUser(String userId, String password) throws UserManager.UsernameOccupiedException, Exception {
		loggedUser = um.createUser(userId, password);
		if (loggedUser == null)
			return false;
		else {
			gotoLogin();
			return true;
		}
	}

	/**
	 * Goto the login screen
	 */
	public void gotoLogin() {
		try {
			LoginController l = (LoginController) replaceSceneContent("login.fxml");
			l.setApp(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Goto the create user screen
	 */
	public void gotoCreateUser() {
		try {
			UserCreateController l = (UserCreateController) replaceSceneContent("create_user.fxml");
			l.setApp(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Goto the create portfolio screen
	 */
	public void gotoCreatePortfolio() {
		try {
			PortfolioCreateController m = (PortfolioCreateController) replaceSceneContent("create_portfolio.fxml");
			m.setApp(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Goto the main screen
	 *
	 * @return the main view controller that will interface with the controller
	 */
	private MainController gotoMain() {
		try {
			MainController m = (MainController) replaceSceneContent("main.fxml");
			return m;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Will take a given .fxml file and inflate it into the view
	 *
	 * @param fxml
	 *            the file to inflate
	 * @return the view controller
	 * @throws Exception
	 */
	private Initializable replaceSceneContent(String fxml) throws Exception {
		fxml = "/" + fxml;
		FXMLLoader loader = new FXMLLoader();
		InputStream in = getClass().getResourceAsStream(fxml);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		loader.setLocation(getClass().getResource(fxml));

		Pane page;
		page = (Pane) loader.load(in);
		in.close();
		Scene scene = new Scene(page);
		stage.setScene(scene);
		stage.sizeToScene();
		return (Initializable) loader.getController();
	}

	public class PortfolioManager {
		private File portfolioFile;

		/**
		 * Create a new PortfolioManager object
		 *
		 * @param portfolioFile
		 *            the file to grab the data from
		 */
		public PortfolioManager(File portfolioFile) {
			this.portfolioFile = portfolioFile;
			try {
				if (!portfolioFile.exists())
					portfolioFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * Get all of the portfolio objects in the file
		 *
		 * @return All of the portfolio objects
		 */
		public ArrayList<Portfolio> getPortfolios() {
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
			} catch (Exception e) {
				System.err.println("Error retrieving portfolios from file");
				e.printStackTrace();
			}
			return out;
		}

		/**
		 * Save all of the given portfolios to file
		 *
		 * @param portfolios
		 *            the stuff to save
		 */
		public void savePortfolios(ArrayList<Portfolio> portfolios) {
			if (portfolios != null) {
				try {
					OutputStream buffer = new BufferedOutputStream(new FileOutputStream(portfolioFile, false));
					ObjectOutputStream stream = new ObjectOutputStream(buffer);
					for (Portfolio p : portfolios)
						stream.writeObject(p);
					buffer.close();
					stream.close();
				} catch (Exception e) {
					System.err.println("Error saving portfolios to file");
					e.printStackTrace();
				}
			}
		}
	}

	private boolean deleteUser(String loginId) {
		List<User> users = UserManager.getAllUsers();
		for (int i = 0; i < users.size(); i++)
			if (users.get(i).username.equals(loginId)) {
				users.remove(i);
				return true;
			}
		return false;
	}

	public class UnassociatedUserException extends Exception {
		public UnassociatedUserException(String message) {
			super(message);
		}
	}
}
