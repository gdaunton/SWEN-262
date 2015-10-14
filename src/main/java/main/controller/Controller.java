package main.controller;


import main.model.Portfolio;
import main.model.User;
import main.view.MainController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Controller {
    private Queue<Command> commandUndoStack = new LinkedList<Command>();
    private Queue<Command> commandBackStack = new LinkedList<Command>();
    private MainController view;
    private User user;
    private OnLogout logoutListener;
    private ArrayList<Portfolio> portfolios;
    public Portfolio currentPortfolio;

    public Controller(ArrayList<Portfolio> portfolios, User user, MainController mainController) {
        this.portfolios = portfolios;
        this.user = user;
        this.view = mainController;
        for(Portfolio p : portfolios){
            if(p.getUsers().contains(user))
                currentPortfolio = p;
        }
        view.setApp(this);
    }

    public ArrayList<Portfolio> getUserPortfolios(User user){
        ArrayList<Portfolio> temp = new ArrayList<Portfolio>();
        for(Portfolio p : this.portfolios){
            if(p.getUsers().contains(user))
                temp.add(p);
        }
        return temp;
    }

    public void executeCommand(Command command) {
        command.execute();
        commandBackStack.add(command);
        view.update();
    }

    public void undo() {
        Command undo = commandBackStack.poll();
        undo.undo();
        commandUndoStack.add(undo);
        view.update();
    }

    public void redo() {
        Command redo = commandUndoStack.poll();
        redo.execute();
        commandUndoStack.add(redo);
        view.update();
    }

    public void setOnLogout(OnLogout handler) {
        this.logoutListener = handler;
    }

    public interface OnLogout {
        void Logout();
    }
}
