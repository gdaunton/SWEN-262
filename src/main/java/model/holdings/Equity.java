package model.holdings;

public abstract class Equity{

    public enum Type{
        STOCK,
        BOND,
        MUTUAL_FUND
    };

    private String tickerSymbol;
    private String name;
    private int shares;
    private double price_per_share;
    private double totalValue;
    private int marketIndex;
    private String industrySector;
    private Type type;

    /**
     * Creates a new Equity Object
     * @param type The type of the Equity
     * @param tickerSymbol the Ticker Symbol for the Equity
     * @param name The name of the Equity
     * @param shares The ammount of shares in the Equity
     * @param price_per_share The price of each share of the Equity
     */
    public Equity(Type type, String tickerSymbol, String name, int shares, double price_per_share) {
        this.tickerSymbol = tickerSymbol;
        this.name = name;
        this.shares = shares;
        this.price_per_share = price_per_share;
        this.totalValue = this.shares * this.price_per_share;
        this.type = type;
    }

    /**
     * Get the ticker symbol
     * @return The ticker symbol
     */
    public String getTickerSymbol(){
        return tickerSymbol;
    }

    /**
     * Get the name of the Equity
     * @return The name of the Equity
     */
    public String getName(){
        return name;
    }

    /**
     * Get the number of shares in the Equity
     * @return The number of shares in the Equity
     */
    public int getShares(){
        return shares;
    }

    /**
     * Set the number of shares in the Equity
     * @param shares The new ammount of shares in the Equity
     */
    public void setShares(int shares) {
        this.shares = shares;
    }

    /**
     * Get the price per share of the Equity
     * @return The price per share of the Equity
     */
    public double getPrice_per_share() {
        return price_per_share;
    }

    /**
     * Set the price per share of the Equity
     * @param price_per_share The new price per share of the Equity
     */
    public void setPrice_per_share(double price_per_share) {
        this.price_per_share = price_per_share;
    }

    /**
     * Get the total value of the Equity
     * @return The total value of the Equity
     */
    public double getTotalValue() {
        return totalValue;
    }

}