package main.model.holdings;

import main.model.util.CSVImporter;
import sun.java2d.cmm.Profile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class HoldingManager {
    public static HashMap<Profile, ArrayList<Holding>> holding_list = null;

    public static void import_equities (File f) throws IOException {
        holding_list = CSVImporter.ImportAllEquity(f);
    }
	
	public static void link_holdings(Profile p) {
		holding_list.put(p, p.getHoldings());
	}

    public static ArrayList<Holding> search(String input, String field_name) throws UnimportedEquitiesException {
        if(holding_list == null) {
            throw new UnimportedEquitiesException("Please make sure that the Equities are imported before calling this function.");
		}
        ArrayList<Holding> out = new ArrayList<Holding>();
        for(Holding h : holding_list) {
			if(h.getField(field_name).contains(input)) { out.add(h); }
        }
        return out;
    }

    public static ArrayList<Holding> filter(String filter) throws UnimportedEquitiesException {
        if(holding_list == null) {
            throw new UnimportedEquitiesException("Please make sure that the Equities are imported before calling this function.");
		}
		
		boolean no_accounts = false;
		boolean no_equities = false;
		
		if(filter.contains('-na')) { no_accounts = true; }
		if(filter.contains('-ne')) { no_equities = true; }
		
        ArrayList<Holding> out = new ArrayList<Holding>();
        for(Holding h : holding_list) {
            if(no_accounts && h instanceof Account) { continue; }
			if(no_equities && h instanceof Equity)  { continue; }
			
			out.add(h);
        }
        return out;
    }

    public static class UnimportedEquitiesException extends Exception {
        public UnimportedEquitiesException(String message){ super(message); }
    }
}