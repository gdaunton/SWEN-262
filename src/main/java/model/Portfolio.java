package model;

import model.holdings.Account;
import model.holdings.Equity;

import java.util.ArrayList;

public class Portfolio{
    private ArrayList<User> users;
    private ArrayList<Account> accounts;
    private ArrayList<Equity> equites;

    public Portfolio(User user) {
        this.users.add(user);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public ArrayList<Equity> getEquites() {
        return equites;
    }

    public void addUser(User user){
        this.users.add(user);
    }

    public void addAccount(Account account){
        this.accounts.add(account);
    }

    public void addEquity(Equity equity){
        this.equites.add(equity);
    }
}