package main.model;

import main.model.holdings.Account;
import main.model.holdings.Equity;
import main.model.holdings.Holding;

import javax.sound.sampled.Port;
import java.io.Serializable;
import java.util.ArrayList;

public class Portfolio implements Serializable{
    private ArrayList<User> users;
    private static ArrayList<Holding> holdings;
    private String name;


    /**
     * Creates a new Portfolio Object
     * @param users The users to attach to this portfolio
     */
    public Portfolio(ArrayList<User> users, String name) {
        this.users = users;
        this.name = name;
        this.holdings = new ArrayList<Holding>();
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
     * @return All of the accounts attached to this portfolio
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
     * @param holding The account to add
     */
    public void addHolding(Holding holding){
        this.holdings.add(holding);
    }

    /**
     * Remove a holding from this portfolio
     * @param holding The account to add
     */
    public void removeHolding(Holding holding){
        this.holdings.remove(holding);
    }

    @Override
    public boolean equals(Object p){
        Portfolio port = (Portfolio)p;
        if(!port.name.equals(this.name))
            return false;
        else{
            for(int i = 0; i < this.holdings.size(); i++){
                if(!port.holdings.get(i).equals(this.holdings.get(i)))
                    return false;
            }
        }
        return true;
    }
	
	public double eval() {
        double total = 0;
        for(Holding h : holdings)
            total += h.getValue();
		return  total;
	}
}