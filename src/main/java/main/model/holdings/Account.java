package main.model.holdings;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Account extends Holding implements Serializable {

    public enum Type {
        BANK, MONEY_MARKET;
		
		public String toString() { return name().toLowerCase().replace('_', ' '); }
    }

    /**
     * Parses the type from a string.
     *
     * @param s The type string.
     * @return The type.
     */
    public static Type typeFromString(String s) {
        if (s.toUpperCase().equals("BANK")) {
            return Type.BANK;
        }
        if (s.toUpperCase().equals("MONEY_MARKET")) {
            return Type.MONEY_MARKET;
        }
        return Type.BANK;
    }

    private String name;
    private double balance;
    private double start_balance;
    public Date opened;
    private Type type;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

    /**
     * Creates a new Account object
     *
     * @param name    The name of the bank
     * @param balance The initial balance in the account
     * @param type    The type of the account
     */
    public Account(String name, double balance, Type type) {
        this.name = name;
        this.balance = balance;
        start_balance = balance;
        this.type = type;
        opened = Calendar.getInstance().getTime();
    }

    /**
     * Get the name of the Bank
     *
     * @return The name of the bank
     */
    public String getName() {
        return name;
    }

    /**
     * Get the account balance
     *
     * @return The account balance
     */
    public double getBalance() {
        return balance;
    }

    public double get_start_balance() {
        return start_balance;
    }

    /**
     * Withdraws the given ammount from the account and returns the remaining
     * balance
     *
     * @param ammount The ammount to withdraw
     * @return The remaining account balance
     */
    public double withdraw(double ammount) {
        if (ammount < 0)
            return this.balance;
        if (balance - ammount < 0)
            return this.balance;
        return this.balance -= ammount;
    }

    /**
     * Deposits the given amount from the account and returns the new balance
     *
     * @param amount The amount to deposit
     * @return The new account balance
     */
    public double deposit(double amount) {
        if (amount < 0)
            return this.balance;
        return this.balance += amount;
    }

    /**
     * Transfers the given ammount to the new account
     *
     * @param amount The ammount to transfer
     * @param account The account to transfer the amount to
     * @return The remaining account balance
     */
    public double transfer(double amount, Account account) {
        if (account == null)
            return this.getBalance();
        if (amount < 0)
            return this.getBalance();
        if (balance - amount < 0)
            return this.getBalance();
        this.withdraw(amount);
        account.deposit(amount);
        return this.getBalance();
    }

    /**
     * Get the type of the account
     *
     * @return The type of the account
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the date the account was opened
     *
     * @return The date the account was opened
     */
    public Date getOpened() {
        return opened;
    }

    /**
     * Gets the opened string.
     *
     * @return The opened string.
     */
    public String getOpenedString() {
        return sdf.format(opened);
    }

    /**
     * Parses a date string.
     *
     * @param s The date string.
     * @return The Date.
     */
    public static Date parseDate(String s) {
        try {
            return sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the balance.
     *
     * @return the balance.
     */
    public double getValue() {
        return getBalance();
    }

    /**
     * If the string matches this given account
     */
    public boolean match(String query) {
        return name.toLowerCase().contains(query.toLowerCase());
    }

    @Override
    public Holding clone() {
        return new Account(name, balance, type);
    }

    @Override
    public String toString() {
        return name;
    }
}