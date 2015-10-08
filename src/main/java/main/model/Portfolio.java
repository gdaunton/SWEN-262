package main.model;

import main.model.holdings.Account;
import main.model.holdings.Equity;
import main.model.holdings.Holding;

import java.util.ArrayList;

public class Portfolio{
    private ArrayList<User> users;
    private ArrayList<Holding> holdings;
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
     * Get all of the holdings attached to this portfolio
     * @return All of the holdings attached to this portfolio
     */
    public ArrayList<Holding> getHoldings() {
        return holdings;
    }

    /**
     * Add a user to this portfolio
     * @param user The user to add
     */
    public void addUser(User user){
        this.users.add(user);
    }

    /**
     * Add a holding to this portfolio
     * @param holding The holding to add
     */
    public void addHolding(Holding holding){
        this.accounts.add(holding);
    }
}