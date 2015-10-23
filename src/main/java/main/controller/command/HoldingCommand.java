package main.controller.command;

import main.model.Portfolio;
import main.model.holdings.Transaction;
import main.model.holdings.Account;
import main.model.holdings.Equity;
import main.model.holdings.Holding;

public class HoldingCommand implements Command {

    public enum Action {
        ADD, DELETE, MODIFY
    }

    public enum Modification {
        SHARES, WITHDRAW, DEPOSIT, TRANSFER
    }

    private Portfolio portfolio;

    private Action type;
    private Holding target;
    private Account dest_account;
    private Modification mod;
    private double modifier;

    /**
     * Sets up the HoldingCommand.
     *
     * @param type      The type of holding.
     * @param portfolio The portfolio with this holding.
     * @param holding   The holding itself.
     */
    public HoldingCommand(Action type, Portfolio portfolio, Holding holding) {
        this.type = type;
        this.portfolio = portfolio;
        this.target = holding;
    }

    /**
     * Sets up the HoldingCommand for an specific action.
     *
     * @param type         The type of holding.
     * @param portfolio    The portfolio with this holding.
     * @param holding      The holding itself.
     * @param modification The aspect to take action on.
     * @param modifier     The action to take.
     */
    public HoldingCommand(Action type, Portfolio portfolio, Holding holding, Modification modification,
                          double modifier) {
        this.type = type;
        this.portfolio = portfolio;
        this.target = holding;
        this.mod = modification;
        this.modifier = modifier;
    }

    public HoldingCommand(Action type, Portfolio portfolio, Account origin, Account destination,
                          Modification modification, double modifier) {
        this.type = type;
        this.portfolio = portfolio;
        this.target = origin;
        this.dest_account = destination;
        this.mod = modification;
        this.modifier = modifier;
    }

    /**
     * Executes the command.
     */
    public void execute() {
        switch (type) {
            case ADD:
                portfolio.addHolding(target);
                portfolio.history.add(new Transaction(Transaction.Type.ADD, new Object[]{target}));
                break;
            case DELETE:
                portfolio.removeHolding(target);
                portfolio.history.add(new Transaction(Transaction.Type.REMOVE, new Object[]{target}));
                break;
            case MODIFY:
                switch (mod) {
                    case SHARES:
                        try {
                            ((Equity) target).setShares((int) Math.round(modifier));
                            portfolio.history.add(new Transaction(Transaction.Type.EQUITY_BUY_SELL, new Object[]{target, (double)Math.round(modifier)}));
                        } catch (ClassCastException e) {
                            System.err.println("Please only give equity objects while using the SHARES modifier");
                            e.printStackTrace();
                        }
                        break;
                    case WITHDRAW:
                        try {
                            ((Account) target).withdraw(modifier);
                            portfolio.history.add(new Transaction(Transaction.Type.ACCOUNT_WITHDRAW, new Object[]{target, modifier}));
                        } catch (ClassCastException e) {
                            System.err.println("Please only give account objects while using the WITHDRAW modifier");
                            e.printStackTrace();
                        }
                        break;
                    case DEPOSIT:
                        try {
                            ((Account) target).deposit(modifier);
                            portfolio.history.add(new Transaction(Transaction.Type.ACCOUNT_DEPOSIT, new Object[]{target, modifier}));
                        } catch (ClassCastException e) {
                            System.err.println("Please only give account objects while using the DEPOSIT modifier");
                            e.printStackTrace();
                        }
                        break;
                    case TRANSFER:
                        try {
                            ((Account) target).transfer(modifier, dest_account);
                            portfolio.history.add(new Transaction(Transaction.Type.ACCOUNT_TRANSFER, new Object[]{target, dest_account, modifier}));
                        } catch (ClassCastException e) {
                            System.err.println("Please only give account objects while using the TRANSFER modifier");
                            e.printStackTrace();
                        }
                        break;
                }
                break;
        }
    }

    /**
     * Undoes the last command.
     */
    public void undo() {
        switch (type) {
            case ADD:
                portfolio.removeHolding(target);
                break;
            case DELETE:
                portfolio.addHolding(target);
                break;
            case MODIFY:
                switch (mod) {
                    case SHARES:
                        try {
                            ((Equity) target).setShares(((Equity) target).getShares() - (int) Math.round(modifier));
                        } catch (ClassCastException e) {
                            System.err.println("Please only give equity objects while using the SHARES modifier");
                            e.printStackTrace();
                        }
                        break;
                    case WITHDRAW:
                        try {
                            ((Account) target).deposit(modifier);
                        } catch (ClassCastException e) {
                            System.err.println("Please only give account objects while using the WITHDRAW modifier");
                            e.printStackTrace();
                        }
                        break;
                    case DEPOSIT:
                        try {
                            ((Account) target).withdraw(modifier);
                        } catch (ClassCastException e) {
                            System.err.println("Please only give account objects while using the DEPOSIT modifier");
                            e.printStackTrace();
                        }
                        break;
                    case TRANSFER:
                        try {
                            dest_account.transfer(modifier, (Account) target);
                        } catch (ClassCastException e) {
                            System.err.println("Please only give account objects while using the TRANSFER modifier");
                            e.printStackTrace();
                        }
                        break;
                }
                break;
        }
        portfolio.history.remove(portfolio.history.size() - 1);
    }
}
