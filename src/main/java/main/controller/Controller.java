package main.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.stage.Stage;
import main.controller.command.Command;
import main.model.Portfolio;
import main.model.user.User;
import main.view.MainController;

public class Controller {
    private List<Command> commandUndoStack = new LinkedList<Command>();
    private List<Command> commandBackStack = new LinkedList<Command>();
    private MainController view;
    private User user;
    private OnLogout logoutListener;
    private ArrayList<Portfolio> portfolios;
    public Portfolio currentPortfolio;
    public Stage stage;

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
    }

    /**
     * Gets portfolios for an user.
     *
     * @param user The user.
     * @return The list of portfolios.
     */
    public ArrayList<Portfolio> getUserPortfolios(User user) {
        ArrayList<Portfolio> temp = new ArrayList<Portfolio>();
        for (Portfolio p : this.portfolios) {
            if (p.getUsers().contains(user))
                temp.add(p);
        }
        return temp;
    }

    public ArrayList<Portfolio> getOtherPortfolios() {
        ArrayList<Portfolio> temp = new ArrayList<Portfolio>();
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
        System.out.println(commandBackStack.toString());
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
        System.out.println("undo: " + commandUndoStack.toString());
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
        void Logout();
    }

    public void update() {
        view.update();
    }
}
