package main.model.holdings;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Transaction implements Serializable {
    public String date;
    public String holding;
    public String amount = "";
    public String type;

    public enum Type {
        ADD,
        REMOVE,
        ACCOUNT_TRANSFER,
        ACCOUNT_DEPOSIT,
        ACCOUNT_WITHDRAW,
        EQUITY_BUY_SELL;

        public String toString() {
            String name = name();
            switch (name) {
                case "ADD":
                    return "Add Holding";
                case "REMOVE":
                    return "Remove Holding";
                case "ACCOUNT_TRANSFER":
                    return "Transfer Between Accounts";
                case "ACCOUNT_DEPOSIT":
                    return "Deposit to Account";
                case "ACCOUNT_WITHDRAW":
                    return "Withdraw from Account";
                case "EQUITY_BUY_SELL":
                    return "Buy or Sell Equity";
            }
            return name;
        }
    }

    /**
     * Creates a new transaction object
     *
     * @param type the type of transaction
     * @param args the arguments
     */
    public Transaction(Type type, Object[] args) {
        Holding h1 = null, h2 = null;
        double amount = 0;
        switch (type) {
            case ADD:
                h1 = (Holding) args[0];
                amount = (Double) args[1];
                if (h1 instanceof Account) {
                    this.amount = NumberFormat.getCurrencyInstance().format(amount);
                } else {
                    double total = ((Equity) h1).getPrice_per_share() * amount;
                    this.amount = Integer.toString((int) amount) + " Share(s) totalling " + NumberFormat.getCurrencyInstance().format(total);
                }
                break;
            case REMOVE:
                h1 = (Holding) args[0];
                this.amount = "N/A";
                break;
            case ACCOUNT_DEPOSIT:
                h1 = (Holding) args[0];
                amount = (Double) args[1];
                this.amount = NumberFormat.getCurrencyInstance().format(amount);
                break;
            case ACCOUNT_TRANSFER:
                h1 = (Holding) args[0];
                h2 = (Holding) args[1];
                amount = (Double) args[2];
                this.amount = NumberFormat.getCurrencyInstance().format(amount);
                break;
            case ACCOUNT_WITHDRAW:
                h1 = (Holding) args[0];
                amount = (Double) args[1];
                this.amount = NumberFormat.getCurrencyInstance().format(amount);
                break;
            case EQUITY_BUY_SELL:
                h1 = (Holding) args[0];
                amount = (Double) args[1];
                if (amount >= 0) {
                    this.amount = "Bought ";
                } else {
                    this.amount = "Sold ";
                    amount = -1 * amount;
                }
                double total = ((Equity) h1).getPrice_per_share() * amount;
                this.amount += Integer.toString((int) amount) + " share(s) totalling " + NumberFormat.getCurrencyInstance().format(total);
                break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy kk:mm");
        date = sdf.format(Calendar.getInstance().getTime());
        String hFinal = h1.toString();
        this.type = type.toString();
        if (h2 != null)
            hFinal += (", " + h2.toString());
        this.holding = hFinal;
    }
}