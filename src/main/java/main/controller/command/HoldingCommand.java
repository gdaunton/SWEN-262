package main.controller.command;

import main.controller.Command;
import main.model.Portfolio;
import main.model.holdings.Account;
import main.model.holdings.Equity;
import main.model.holdings.Holding;

public class HoldingCommand implements Command {

    public enum Action{
        ADD,
        DELETE,
        MODIFY
    }

    public enum Modification {
        SHARES,
        WITHDRAW,
        DEPOSIT,
        TRANSFER
    }

    private Portfolio portfolio;

    private Action type;
    private Holding target;
    private Account dest_account;
    private Modification mod;
    private double modifier;

    public HoldingCommand(Action type, Portfolio portfolio, Holding holding){
        this.type = type;
        this.portfolio = portfolio;
        this.target = holding;
    }

    public HoldingCommand(Action type, Portfolio portfolio, Holding holding, Modification modification, double modifier){
        this.type = type;
        this.portfolio = portfolio;
        this.target = holding;
        this.mod = modification;
        this.modifier = modifier;
    }

    public HoldingCommand(Action type, Portfolio portfolio, Account origin, Account destination, Modification modification, double modifier){
        this.type = type;
        this.portfolio = portfolio;
        this.target = origin;
        this.dest_account = destination;
        this.mod = modification;
        this.modifier = modifier;
    }

    public void execute() {
        switch(type){
            case ADD:
                portfolio.addHolding(target);
            case DELETE:
                portfolio.removeHolding(target);
            case MODIFY:
                switch(mod){
                    case SHARES:
                        try {
                            ((Equity) target).setShares((int)Math.round(modifier));
                        } catch(ClassCastException e){
                            System.err.println("Please only give equity objects while using the SHARES modifier");
                        }
                    case WITHDRAW:
                        try {
                            ((Account) target).withdraw(modifier);
                        } catch(ClassCastException e){
                            System.err.println("Please only give account objects while using the WITHDRAW modifier");
                        }
                    case DEPOSIT:
                        try {
                            ((Account) target).deposit(modifier);
                        } catch(ClassCastException e){
                            System.err.println("Please only give account objects while using the DEPOSIT modifier");
                        }
                    case TRANSFER:
                        try {
                            ((Account) target).transfer(modifier, dest_account);
                        } catch(ClassCastException e){
                            System.err.println("Please only give account objects while using the TRANSFER modifier");
                        }
                }
        }
    }

    public void undo() {
        switch(type){
            case ADD:
                portfolio.removeHolding(target);
            case DELETE:
                portfolio.addHolding(target);
            case MODIFY:
                switch(mod){
                    case SHARES:
                        try {
                            ((Equity) target).setShares(((Equity) target).getShares() - (int) Math.round(modifier));
                        } catch(ClassCastException e){
                            System.err.println("Please only give equity objects while using the SHARES modifier");
                        }
                    case WITHDRAW:
                        try {
                            ((Account) target).deposit(modifier);
                        } catch(ClassCastException e){
                            System.err.println("Please only give account objects while using the WITHDRAW modifier");
                        }
                    case DEPOSIT:
                        try {
                            ((Account) target).withdraw(modifier);
                        } catch(ClassCastException e){
                            System.err.println("Please only give account objects while using the DEPOSIT modifier");
                        }
                    case TRANSFER:
                        try {
                            dest_account.transfer(modifier, (Account)target);
                        } catch(ClassCastException e){
                            System.err.println("Please only give account objects while using the TRANSFER modifier");
                        }
                }
        }
    }
}
