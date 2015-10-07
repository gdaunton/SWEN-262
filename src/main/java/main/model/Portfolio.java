package main.model;

import main.model.holdings.Account;
import main.model.holdings.Equity;

import java.util.ArrayList;

public class Portfolio{
    private ArrayList<User> users;
    private ArrayList<Account> accounts;
    private ArrayList<Equity> equites;
    private String name;


    /**
     * Creates a new Portfolio Object
     * @param user The user to attach to this portfolio
     */
    public Portfolio(User user, String name) {
        this.users.add(user);
        this.name = name;
    }

    /**
     * Get all of the users attached to this portfolio
     * @return All of the users attached to this portfolio
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * Get all of the accounts attached to this portfolio
     * @return All of the accounts attached to this portfolio
     */
    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    /**
     * Get all of the equities attached to this portfolio
     * @return All of the equities attached to this portfolio
     */
    public ArrayList<Equity> getEquites() {
        return equites;
    }

    /**
     * Add a user to this portfolio
     * @param user The user to add
     */
    public void addUser(User user){
        this.users.add(user);
    }

    /**
     * Add a account to this portfolio
     * @param account The account to add
     */
    public void addAccount(Account account){
        this.accounts.add(account);
    }

    /**
     * Add a equity to this portfolio
     * @param equity The equity to add
     */
    public void addEquity(Equity equity){
        this.equites.add(equity);
    }
}