package model.util;


import model.holdings.Equity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVImporter {

    public static ArrayList<Equity> ImportAllEquity(File file) throws IOException {
        ArrayList<Equity> new_equity = new ArrayList<Equity>();
        CSVParser parser = CSVFormat.RFC4180.parse(new FileReader(file));
        for(CSVRecord r : parser){
            ArrayList<String> markets = new ArrayList<String>();
            for(int i = 2; i <= r.size(); i++)
                markets.add(r.get(i));
            new_equity.add(new Equity(Equity.Type.STOCK, r.get(0), r.get(1), 0, Double.parseDouble(r.get(2)), markets));
        }
        return new_equity;
    }
}
