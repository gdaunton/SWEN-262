package main.model.holdings;

import java.io.Serializable;
import java.util.ArrayList;

public class Equity extends Holding implements Serializable {

    public enum Type {
        STOCK, BOND, MUTUAL_FUND;

        public String toString() {
            return name().toLowerCase().replace('_', ' ');
        }
    }

    /**
     * Parses the type from a string.
     *
     * @param s The string.
     * @return The type.
     */
    public static Type typeFromString(String s) {
        if (s.toUpperCase().equals("STOCK")) {
            return Type.STOCK;
        }
        if (s.toUpperCase().equals("BOND")) {
            return Type.BOND;
        }
        if (s.toUpperCase().equals("MUTUAL_FUND")) {
            return Type.MUTUAL_FUND;
        }
        return Type.STOCK;
    }

    private String tickerSymbol;
    private String name;                    //company name
    private int shares;
    private double price_per_share;
    private double old_price;
    public Type type;
    private ArrayList<String> marketSectors;

    /**
     * Creates a new Equity Object
     *
     * @param type            The type of the Equity
     * @param tickerSymbol    the Ticker Symbol for the Equity
     * @param name            The name of the Equity
     * @param shares          The ammount of shares in the Equity
     * @param price_per_share The price of each share of the Equity
     * @param marketSectors   The different markets that the Equity can be in
     */
    public Equity(Type type, String tickerSymbol, String name, int shares, double price_per_share,
                  ArrayList<String> marketSectors) {
        this.tickerSymbol = tickerSymbol;
        this.name = name;
        this.shares = shares;
        this.price_per_share = price_per_share;
        old_price = price_per_share;
        this.type = type;
        this.marketSectors = marketSectors;
    }

    /**
     * Get the ticker symbol
     *
     * @return The ticker symbol
     */
    public String getTickerSymbol() {
        return tickerSymbol;
    }

    /**
     * Get the name of the Equity
     *
     * @return The name of the Equity
     */
    public String getName() {
        return name;
    }

    /**
     * Get the number of shares in the Equity
     *
     * @return The number of shares in the Equity
     */
    public int getShares() {
        return shares;
    }

    /**
     * Set the number of shares in the Equity
     *
     * @param shares The new ammount of shares in the Equity
     */
    public void setShares(int shares) {
        this.shares = shares;
    }

    /**
     * Get the price per share of the Equity
     *
     * @return The price per share of the Equity
     */
    public double getPrice_per_share() {
        return price_per_share;
    }

    /**
     * Get the old price.
     *
     * @return The old price.
     */
    public double get_old_price() {
        return old_price;
    }

    /**
     * Set the price per share of the Equity
     *
     * @param price_per_share The new price per share of the Equity
     */
    public void setPrice_per_share(double price_per_share) {
        old_price = this.price_per_share;
        this.price_per_share = price_per_share;
        this.notifyObservers(price_per_share);
        this.setChanged();
    }

    /**
     * Get the total value of the Equity
     *
     * @return The total value of the Equity
     */
    public double getValue() {
        return price_per_share * shares;
    }

    /**
     * Get the array of Market Sectors that this Equity is in
     *
     * @return The array of Market Sectors that this Equity is in
     */
    public ArrayList<String> getMarketSectors() {
        return marketSectors;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * If the query matches this equity
     *
     * @param query The query to search for
     * @return
     */
    public boolean match(String query) {
        for (String s : marketSectors)
            if (s.contains(query))
                return true;
        return tickerSymbol.toLowerCase().contains(query.toLowerCase()) || name.toLowerCase().contains(query.toLowerCase());
    }

    /**
     * Get a clone of this equity
     *
     * @return a clone of this equity
     */
    public Equity clone() {
        return new Equity(type, tickerSymbol, name, shares, price_per_share, marketSectors);
    }
}