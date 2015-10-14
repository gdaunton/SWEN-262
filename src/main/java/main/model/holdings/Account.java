package main.model.holdings;

import java.util.Calendar;
import java.util.Date;

public class Account extends Holding {

    public enum Type{
        BANK,
        MONEY_MARKET
    };
	
	public static Type typeFromString(String s) {
		if(s.toUpperCase().equals("BANK")) { return Type.BANK; }
		if(s.toUpperCase().equals("MONEY_MARKET")) { return Type.MONEY_MARKET; }
		return Type.BANK;
	}

    private String name;
    private double balance;
    private Date opened;
    private Type type;

    /**
     * Creates a new Account object
     * @param name The name of the bank
     * @param balance The initial balance in the account
     * @param type The type of the account
     */
    public Account(String name, double balance, Type type) {
        this.name = name;
        this.balance = balance;
        this.type = type;
        opened = Calendar.getInstance().getTime();
    }

    /**
     * Get the name of the Bank
     * @return The name of the bank
     */
    public String getName() {
        return name;
    }

    /**
     * Get the account balance
     * @return The account balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Withdraws the given ammount from the account and returns the remaining balance
     * @param ammount The ammount to withdraw
     * @return The remaining account balance
     */
    public double withdraw(double ammount) {
        if(ammount < 0)
            return this.balance;
        if(balance - ammount < 0)
            return this.balance;
        return this.balance -= ammount;
    }

    /**
     * Deposits the given ammount from the account and returns the new balance
     * @param ammount The ammount to deposit
     * @return The new account balance
     */
    public double deposit(double ammount) {
        if(ammount < 0)
            return this.balance;
        return this.balance += ammount;
    }

    /**
     * Transfers the given ammount to the new account
     * @param ammount The ammount to transfer
     * @param account The account to transfer the ammount to
     * @return The remaining account balance
     */
    public double transfer(double ammount, Account account) {
        if(account == null)
            return this.getBalance();
        if(ammount < 0)
            return this.getBalance();
        if(balance - ammount < 0)
            return this.getBalance();
        this.withdraw(ammount);
        account.deposit(ammount);
        return this.getBalance();
    }

    /**
     * Get the type of the account
     * @return The type of the account
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the date the account was opened
     * @return The date the account was opened
     */
    public Date getOpened() {
        return opened;
    }

    public double getValue() {
        return getBalance();
    }

    public class OverDrawException extends Exception{
        public OverDrawException(String message){
            super(message);
        }
    }

    public class NegativeValueException extends Exception{
        public NegativeValueException(String message){
            super(message);
        }
    }

    public class NullAccountException extends Exception{
        public NullAccountException(String message){
            super(message);
        }
    }
}