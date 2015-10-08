package model.holdings

import java.util.HashMap;
import java.util.ArrayList;

import model.util.CSVImporter

public class HoldingManager {
    static HashMap<Portfolio, ArrayList<Holding>> owned_holding_lists = new HashMap<Portfolio, Holding>();
    public static ArrayList<Equity> equity_list = null;
    
    public static void grant(Portfolio p, Holding h) {
        owned_holding_lists.get(p).add(h);
    }
    public static void revoke(Portfolio p, Holding h) {
        owned_holding_lists.get(p).remove(h);
    }
    
    public static void import_equities (File f) throws IOException {
        equity_list = CSVImporter.ImportAllEquity(f);
    }
    
    public static void link_equity_list(Portfolio p) {
        //given p, link the Portfolio p to its Holding list in the owned_holding_lists map
        
    }
    
    public static ArrayList<Holding> get_holdings(Portfolio p) {
        return owned_holding_lists.get(p);
    }
}