package main.model.holdings;

import main.model.util.CSVImporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HoldingManager {
    public static ArrayList<Equity> equity_list = null;

    public static enum Filter{
        TICKER,
        //TODO add more filters
    }

    public static void import_equities (File f) throws IOException {
        equity_list = CSVImporter.ImportAllEquity(f);
    }

    public static ArrayList<Equity> search(String input) throws UnimportedEquities{
        if(equity_list == null)
            throw new UnimportedEquities("Please make sure that the Equities are imported before calling this funciton.");
        //TODO do the search function
        return null;
    }

    public static ArrayList<Equity> filter(Filter filter) throws UnimportedEquities {
        if(equity_list == null)
            throw new UnimportedEquities("Please make sure that the Equities are imported before calling this funciton.");
        //TODO do filtering
        return null;
    }

    public static class UnimportedEquities extends Exception{
        public UnimportedEquities(String message){
            super(message);
        }
    }
}