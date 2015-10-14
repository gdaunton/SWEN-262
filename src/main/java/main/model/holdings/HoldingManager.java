package main.model.holdings;

import main.model.Portfolio;
import main.model.util.CSVImporter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class HoldingManager {
    public static HashMap<Portfolio, ArrayList<Holding>> holding_list = new HashMap<Portfolio, ArrayList<Holding>>();
	public static ArrayList<Equity> equities_list = null;

    public static void import_equities (File f) throws IOException {
        equities_list = CSVImporter.importAllEquity(f);
    }
	
	public static void link_holdings(Portfolio p) {
		holding_list.put(p, p.getHoldings());
	}
	
	public static Equity get_by_ticker(String ticker) {
		for(Equity e : equities_list) {
			if(e.getTickerSymbol().equals(ticker)) { return e; }
		}
		return null;
	}

    public static ArrayList<Holding> search(String input, String field_name, Portfolio p) throws IllegalAccessException {
		try {
			ArrayList<Holding> out = new ArrayList<Holding>();
			for (Holding h : holding_list.get(p)) {
				ArrayList<Field> fields = null;
				if (h instanceof Equity) {
					fields = new ArrayList<Field>(Arrays.asList(((Equity) h).getClass().getFields()));
//				if(((Equity)h).getField(field_name).get(h).toString().contains(input)) { out.add(h); }
				} else if (h instanceof Account) {
					fields = new ArrayList<Field>(Arrays.asList(((Account) h).getClass().getFields()));
//				if(((Account)h).getField(field_name).get(h).toString().contains(input)) { out.add(h); }
				} else {
					fields = new ArrayList<Field>(Arrays.asList(h.getClass().getFields()));
//				if(h.getField(field_name).get(h).toString().contains(input)) { out.add(h); }
				}
				for (Field f : fields) {
					if (!f.getName().contains(field_name)) {
						fields.remove(f);
					}
				}
				for (Field f : fields) {
					if (f.get(h).toString().contains(input)) {
						out.add(h);
					}
				}
			}
			return out;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

	public static ArrayList<Holding> searchAll(String input, String field_name) throws UnimportedEquitiesException {
		if(equities_list == null) {
			throw new UnimportedEquitiesException("Please make sure that the Equities are imported before calling this function.");
		}
		try {
			ArrayList<Holding> out = new ArrayList<Holding>();
			for (Holding h : equities_list) {
				ArrayList<Field> fields = null;
				if (h instanceof Equity) {
					fields = new ArrayList<Field>(Arrays.asList(((Equity) h).getClass().getFields()));
//				if(((Equity)h).getField(field_name).get(h).toString().contains(input)) { out.add(h); }
				} else if (h instanceof Account) {
					fields = new ArrayList<Field>(Arrays.asList(((Account) h).getClass().getFields()));
//				if(((Account)h).getField(field_name).get(h).toString().contains(input)) { out.add(h); }
				} else {
					fields = new ArrayList<Field>(Arrays.asList(h.getClass().getFields()));
//				if(h.getField(field_name).get(h).toString().contains(input)) { out.add(h); }
				}
				for (Field f : fields) {
					if (!f.getName().contains(field_name)) {
						fields.remove(f);
					}
				}
				for (Field f : fields) {
					if (f.get(h).toString().contains(input)) {
						out.add(h);
					}
				}
			}
			return out;
		} catch(IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
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