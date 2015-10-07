package model.holdings

import java.util.HashMap;
import java.util.ArrayList;

public class HoldingManager {
    static HashMap<Portfolio, ArrayList<Holding>> owned_holding_lists = new HashMap<Portfolio, Holding>();
    public static ArrayList<Equity> equity_list = null;
    
    public static void grant(Portfolio p, Holding h) { owned_holding_lists.get(p).add(h); }
    public static void revoke(Portfolio p, Holding h){ owned_holding_lists.get(p).remove(h); }
}