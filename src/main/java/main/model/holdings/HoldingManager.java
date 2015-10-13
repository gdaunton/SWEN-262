package main.model.holdings;

import main.model.Portfolio;
import main.model.util.CSVImporter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class HoldingManager {
    public static HashMap<Portfolio, ArrayList<Holding>> holding_list = new HashMap<Portfolio, ArrayList<Holding>>;
	public static ArrayList<Equity> equities_list = null;

    public static void import_equities (File f) throws IOException {
        equities_list = CSVImporter.ImportAllEquity(f);
    }
	
	public static void link_holdings(Portfolio p) {
		holding_list.put(p, p.getHoldings());
	}

    public static ArrayList<Holding> search(String input, String field_name, Portfolio p) throws UnimportedEquitiesException, IllegalAccessException {
        if(equities_list == null) {
            throw new UnimportedEquitiesException("Please make sure that the Equities are imported before calling this function.");
		}
        ArrayList<Holding> out = new ArrayList<Holding>();
        for(Holding h : holding_list.get(p)) {
			if(h instanceof Equity) {
				ArrayList<Field> fields = new ArrayList<Field>((Equity)h).getClass().getFields());
				for(Field f : fields) { if(! f.getName().contains(field_name)) { fields.remove(f); } }
				for(Field f : fields) {
					if(f.get(h).toString().contains(input)) { out.add(h); }
				}
//				if(((Equity)h).getField(field_name).get(h).toString().contains(input)) { out.add(h); }
			}
			else if(h instanceof Account) {
				ArrayList<Field> fields = new ArrayList<Field>(((Account)h).getClass().getFields());
				for(Field f : fields) { if(! f.getName().contains(field_name)) { fields.remove(f); } }
				for(Field f : fields) {
					if(f.get(h).toString().contains(input)) { out.add(h); }
				}
//				if(((Account)h).getField(field_name).get(h).toString().contains(input)) { out.add(h); }
			}
			else {
				ArrayList<Field> fields = new ArrayList<Field>(h.getClass().getFields());
				for(Field f : fields) { if(! f.getName().contains(field_name)) { fields.remove(f); } }
				for(Field f : fields) {
					if(f.get(h).toString().contains(input)) { out.add(h); }
				}
//				if(h.getField(field_name).get(h).toString().contains(input)) { out.add(h); }
			}
        }
        return out;
    }

    public static ArrayList<Holding> filter(String filter, Portfolio p) throws UnimportedEquitiesException {
        if(equities_list == null) {
            throw new UnimportedEquitiesException("Please make sure that the Equities are imported before calling this function.");
		}
		
		boolean no_accounts = false;
		boolean no_equities = false;
		
		if(filter.contains("-na")) { no_accounts = true; }
		if(filter.contains("-ne")) { no_equities = true; }
		
        ArrayList<Holding> out = new ArrayList<Holding>();
        for(Holding h : holding_list.get(p)) {
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