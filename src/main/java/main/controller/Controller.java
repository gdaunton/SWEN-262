package main.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javafx.application.Platform;
import javafx.stage.Stage;
import main.controller.command.Command;
import main.model.Portfolio;
import main.model.holdings.HoldingManager;
import main.model.user.User;
import main.view.MainController;

import javax.xml.parsers.ParserConfigurationException;

public class Controller {
    private List<Command> commandUndoStack = new LinkedList<>();
    private List<Command> commandBackStack = new LinkedList<>();
    private MainController view;
    private User user;
    private OnLogout logoutListener;
    private ArrayList<Portfolio> portfolios;
    public Portfolio currentPortfolio;
    public Stage stage;
    private ScheduledFuture<?> pollerHandle = null;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int poll_cooldown_sec = 60;

    /**
     * Initializes the controller.
     *
     * @param portfolios     The list of portfolios.
     * @param user           The logged in user.
     * @param mainController The main controller.
     */
    public Controller(ArrayList<Portfolio> portfolios, User user, MainController mainController, Stage mainStage) {
        this.portfolios = portfolios;
        this.user = user;
        this.stage = mainStage;
        this.view = mainController;
        for (Portfolio p : portfolios) {
            if (p.getUsers().contains(user)) {
                currentPortfolio = p;
                break;
            }
        }
        view.setApp(this);
        setPollerRefreashRate(poll_cooldown_sec);
    }

    public void setPollerRefreashRate(int rate) {
        if(pollerHandle != null)
            pollerHandle.cancel(false);
        poll_cooldown_sec = rate;
        Runnable poller = () -> {
            try {
                HoldingManager.import_equities_yahoo();
                Platform.runLater(view::update);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        pollerHandle = scheduler.scheduleAtFixedRate(poller, poll_cooldown_sec, poll_cooldown_sec, SECONDS);
    }

    public int getPollerRefreashRate() {
        return poll_cooldown_sec;
    }

    /**
     * Gets portfolios for an user.
     *
     * @param user The user.
     * @return The list of portfolios.
     */
    public ArrayList<Portfolio> getUserPortfolios(User user) {
        ArrayList<Portfolio> temp = new ArrayList<>();
        for (Portfolio p : this.portfolios) {
            if (p.getUsers().contains(user))
                temp.add(p);
        }
        return temp;
    }

    public ArrayList<Portfolio> getOtherPortfolios() {
        ArrayList<Portfolio> temp = new ArrayList<>();
        for (Portfolio p : this.portfolios) {
            if (p != currentPortfolio && p.getUsers().contains(user))
                temp.add(p);
        }
        return temp;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.currentPortfolio = portfolio;
        view.changePortfolio();
    }

    public User getUser() {
        return user;
    }

    /**
     * Executes a command.
     *
     * @param command The command.
     */
    public void executeCommand(Command command) {
        command.execute();
        commandBackStack.add(command);
        view.update();
    }

    /**
     * Undoes a command.
     */
    public void undo() {
        Command undo = commandBackStack.remove(0);
        if (undo != null) {
            undo.undo();
            commandUndoStack.add(undo);
            view.update();
        }
    }

    /**
     * Redoes a command.
     */
    public void redo() {
        Command redo = commandUndoStack.remove(0);
        if (redo != null) {
            redo.execute();
            commandBackStack.add(redo);
            view.update();
        }
        System.out.println("redo: " + commandBackStack.toString());
    }

    public void logout() {
        logoutListener.logout();
    }

    public boolean canUndo() {
        return commandBackStack.size() != 0;
    }

    public boolean canRedo() {
        return commandUndoStack.size() != 0;
    }

    /**
     * Sets the logout handler.
     *
     * @param handler The handler.
     */
    public void setOnLogout(OnLogout handler) {
        this.logoutListener = handler;
    }

    public interface OnLogout {
        void logout();
    }

    public void update() {
        view.update();
    }
}
