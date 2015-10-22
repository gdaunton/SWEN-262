package main.model;

import main.model.holdings.Account;
import main.model.holdings.Equity;
import main.model.holdings.Holding;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Portfolio implements Serializable {
    private ArrayList<User> users;
    private ArrayList<Holding> holdings;
    public String name;
    public ArrayList<Transaction> history = new ArrayList<Transaction>();


    /**
     * Creates a new Portfolio Object
     *
     * @param users The users to attach to this portfolio
     */
    public Portfolio(ArrayList<User> users, String name) {
        this.users = users;
        this.name = name;
        this.holdings = new ArrayList<Holding>();
    }

    /**
     * Get all of the users attached to this portfolio
     *
     * @return All of the users attached to this portfolio
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * Get all of the holdings attached to this portfolio
     *
     * @return All of the accounts attached to this portfolio
     */
    public ArrayList<Holding> getHoldings() {
        return holdings;
    }

    /**
     * Add a user to this portfolio
     *
     * @param user The user to add
     */
    public void addUser(User user) {
        this.users.add(user);
    }

    /**
     * Add a holding to this portfolio
     *
     * @param holding The account to add
     */
    public void addHolding(Holding holding) {
        this.holdings.add(holding);
    }

    /**
     * Remove a holding from this portfolio
     *
     * @param holding The account to add
     */
    public void removeHolding(Holding holding) {
        this.holdings.remove(holding);
    }

    @Override
    public boolean equals(Object p) {
        if (p == null)
            return false;
        Portfolio port = (Portfolio) p;
        if (!port.name.equals(this.name))
            return false;
        else if (holdings.size() != port.holdings.size())
            return false;
        else {
            for (int i = 0; i < this.holdings.size(); i++) {
                if (!port.holdings.get(i).equals(this.holdings.get(i)))
                    return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public double eval_equities() {
        double total = 0;
        for (Holding h : holdings) {
            if (h instanceof Equity) {
                total += h.getValue();
            }
        }
        return total;
    }

    public double eval_accounts() {
        double total = 0;
        for (Holding h : holdings) {
            if (h instanceof Account) {
                total += h.getValue();
            }
        }
        return total;
    }

    /**
     * Get the total value of this portfolio
     *
     * @return the total value of the portfolio
     */
    public double eval() {
        return eval_accounts() + eval_equities();
    }

    /**
     * Import holdings from file (our format)
     *
     * @param f the file to import
     */
    public void import_holdings(File f) {
        import_holdings(f, false);
    }

    /**
     * Import holdings from file
     *
     * @param f         the file to import
     * @param clear_old whether or not to clear old holdings
     */
    public void import_holdings(File f, boolean clear_old) {
        import_holdings(f, clear_old, false);
    }

    /**
     * Import holdings from file
     *
     * @param f          the file to import
     * @param clear_old  whether or not to clear old holdings
     * @param own_format whether or not to use our format
     */
    public void import_holdings(File f, boolean clear_old, boolean own_format) {
        try {
            if (own_format) {
                import_holdings_o(f, clear_old);
            } else {
                import_holdings_e(f, clear_old);
            }
        } catch (Exception e) {
        }
    }

    /**
     * Import team e's holdings
     *
     * @param f         the file to import
     * @param clear_old clear old holdings
     * @throws IOException
     */
    private void import_holdings_e(File f, boolean clear_old) throws IOException {
        if (clear_old) {
            holdings.clear();
        }

        BufferedReader br = new BufferedReader(new FileReader(f));
        String str = null;
        while ((str = br.readLine()) != null) {
            String[] fs = str.split(",");
            if (fs.length > 4) { //equity
                String ticker = fs[0];
                String shares = fs[1];
                String currPr = fs[3];

                ArrayList<String> sectors = new ArrayList<String>();

                holdings.add(new Equity(Equity.Type.STOCK, ticker, ticker, Integer.parseInt(shares), Double.parseDouble(currPr), sectors));
            } else {                //account
                String acName = fs[0];
                String balnce = fs[2];
                String dateSt = fs[3];

                Account a = new Account(acName, Double.parseDouble(balnce), Account.Type.BANK);
                SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
                try {
                    a.opened = sdf.parse(dateSt);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                holdings.add(a);
            }
        }
    }

    /**
     * Import our holdings
     *
     * @param f         the file to import
     * @param clear_old clear old holdings
     * @throws IOException
     */
    private void import_holdings_o(File f, boolean clear_old) throws IOException {
        if (clear_old) {
            holdings.clear();
        }
        BufferedReader br = new BufferedReader(new FileReader(f));
        String str = null;
        while ((str = br.readLine()) != null) {
            String[] fs = str.split(",");
            if (fs.length > 5) { //equity
                String ticker = fs[0];
                String shares = fs[1];
                String currPr = fs[2];

                String eqName = fs[3];
                String eqType = fs[4];
                ArrayList<String> sectors = new ArrayList<String>();
                for (int i = 5; i < fs.length; ++i) {
                    sectors.add(fs[i]);
                }

                holdings.add(new Equity(Equity.typeFromString(eqType), ticker, eqName, Integer.parseInt(shares), Double.parseDouble(currPr), sectors));
            } else {                //account
                String acName = fs[0];
                String acType = fs[1];
                String balnce = fs[2];
                String dateSt = fs[3];

                Account a = new Account(acName, Double.parseDouble(balnce), Account.typeFromString(acType));
                a.opened = Account.parseDate(dateSt);
                holdings.add(a);
            }
        }
    }

    /**
     * Export holdings to file
     *
     * @param f the file to export to
     */
    public void export_holdings(File f) {
        export_holdings(f, true);
    }

    /**
     * Export holdings to file
     *
     * @param f          the file to export to
     * @param own_format whether or not to use our format
     */
    public void export_holdings(File f, boolean own_format) {
        try {
            if (own_format) {
                export_holdings_o(f);
            } else {
                export_holdings_e(f);
            }
        } catch (Exception e) {
        }
    }

    /**
     * Export holdings our way
     *
     * @param f the file to export to
     * @throws IOException
     */
    private void export_holdings_o(File f) throws IOException {
        FileWriter fw = new FileWriter(f);
        boolean first = true;
        for (Holding h : holdings) {
            if (!first) {
                fw.write("\n");
            }
            first = false;
            System.out.print("* ");

            String line = "";
            if (h instanceof Equity) {
                Equity e = (Equity) h;
                line = line.concat(e.getTickerSymbol());
                line = line.concat("," + e.getShares());
                line = line.concat("," + e.getPrice_per_share());
                line = line.concat("," + e.getName());
                line = line.concat("," + e.type);

                for (String s : e.getMarketSectors()) {
                    line = line.concat("," + s);
                }
            } else {
                Account a = (Account) h;
                line = line.concat(a.getName());
                line = line.concat("," + a.getType());
                line = line.concat("," + a.getBalance());

                line = line.concat("," + a.getOpenedString());
            }
            System.out.println("O: " + line);
            fw.write(line);
        }
        fw.close();
    }

    /**
     * Export holdings team e's way
     *
     * @param f the file to export to
     * @throws IOException
     */
    private void export_holdings_e(File f) throws IOException {
        FileWriter fw = new FileWriter(f);
        boolean first = true;
        for (Holding h : holdings) {
            if (!first) {
                fw.write("\n");
            }
            first = false;

            String line = "";
            if (h instanceof Equity) {
                System.out.print("E");
                Equity e = (Equity) h;
                line = line.concat(e.getTickerSymbol());
                line = line.concat("," + e.getShares());
                line = line.concat("," + e.get_old_price());
                line = line.concat("," + e.getPrice_per_share());
                SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
                line = line.concat("," + sdf.format(Calendar.getInstance().getTime()));
            } else if (h instanceof Account) {
                System.out.print("A");
                Account a = (Account) h;
                line = line.concat(a.getName());
                line = line.concat("," + a.get_start_balance());
                line = line.concat("," + a.getBalance());
                SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
                if (a.getOpened() != null) {
                    line = line.concat("," + sdf.format(a.getOpened()));
                } else {
                    line = line.concat("," + sdf.format(Calendar.getInstance().getTime()));
                }
            } else {
                System.out.print("??? - " + h.getClass().getName() + " - ");
            }
            System.out.println("E: " + line);
            fw.write(line);
        }
        fw.close();
    }

    /**
     * Make a clone!
     *
     * @return the clone of this portfolio
     */
    public Portfolio clone() {
        //TODO: return a *DEEP* copy of this Portfolio
        return null;
    }
}