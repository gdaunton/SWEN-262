package main.model.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import main.model.holdings.Equity;

public class CSVImporter {

    /**
     * Imports all equities from a CSV file.
     *
     * @param file The CSV file.
     * @return The list of equities.
     * @throws IOException If an I/O error occurs.
     */
    public static ArrayList<Equity> importAllEquity(File file) throws IOException {
        ArrayList<Equity> new_equity = new ArrayList<Equity>();
        CSVParser parser = CSVFormat.RFC4180.parse(new FileReader(file));
        for (CSVRecord r : parser) {
            ArrayList<String> markets = new ArrayList<String>();
            for (int i = 3; i < r.size(); i++)
                markets.add(r.get(i));
            new_equity.add(new Equity(Equity.Type.STOCK, r.get(0), r.get(1), 0, Double.parseDouble(r.get(2)), markets));
        }
        return new_equity;
    }
}
