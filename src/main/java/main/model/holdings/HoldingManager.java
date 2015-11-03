package main.model.holdings;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import main.model.Portfolio;
import main.model.util.CSVImporter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class HoldingManager {
    public static HashMap<Portfolio, ArrayList<Holding>> holding_list = new HashMap<Portfolio, ArrayList<Holding>>();
    public static ArrayList<Equity> equities_list = null;
    public static final ArrayList<String> djiaTickers = new ArrayList<>(Arrays.asList("AAPL", "AXP", "BA", "CAT", "CSCO", "CVX", "DD", "DIS", "DE", "GS", "HD", "IBM", "INTC", "JNJ", "JPM", "KO", "MCD", "MMM", "MRK", "MSFT", "NKE", "PFE", "PG", "TRV", "UNH", "UTX", "V", "VZ", "WMT", "XOM"));

    /**
     * Imports equities from a file.
     *
     * @param f The file.
     * @throws IOException If an I/O error occurs.
     */
    public static void import_equities(File f) throws IOException {
        equities_list = CSVImporter.importAllEquity(f);
    }

    /**
     * Imports a list of equities form yahoo finance
     *
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static void import_equities_yahoo() throws IOException, ParserConfigurationException {
        String dataRoot = "data/";
        String url = "";
        url = "http://query.yahooapis.com/v1/public/yql?q=select symbol, LastTradePriceOnly, Name, StockExchange from yahoo.finance.quotes where symbol in ";
        if (equities_list == null) {
            File equities = new File(dataRoot + "equities.csv");
            if (equities.exists())
                equities_list = CSVImporter.importAllEquity(equities);
        }
        url = url + "(" + "\"AAPL\"";
        for (Equity e : equities_list) {
            if (e.getTickerSymbol().equals("AAPL")) {
                continue;
            }
            url = url + ", \"" + e.getTickerSymbol() + "\"";
        }
        url = url + ")";
        url = url + "&env=store://datatables.org/alltableswithkeys";
        url = url.replace(" ", "%20").replace("\"", "%22").replace(",", "%2C");

        // Create a URL and open a connection
        URL YahooURL = new URL(url);
        HttpURLConnection con = (HttpURLConnection) YahooURL.openConnection();

        // Set the HTTP Request type method to GET
        con.setRequestMethod("GET");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);

        // Created a BufferedReader to read the contents of the request.
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        //Load and Parse the XML document
        //document contains the complete XML as a Tree.
        Document document = loadXMLFromString(response.toString());

        ArrayList<Equity> temp_list = new ArrayList<Equity>();
        //Iterating through the nodes and extracting the data.
        NodeList nodeList = document.getDocumentElement().getChildNodes().item(0).getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node eq = nodeList.item(i);

            if (eq.getNodeName().equals("quote")) {
                String sym = eq.getAttributes().getNamedItem("symbol").getNodeValue();
                String name = null;
				ArrayList<String> sx = new ArrayList<String>();
                double eqv = -1;

                NodeList fields = eq.getChildNodes();
                for (int j = 0; j < fields.getLength(); j++) {
                    Node f = fields.item(j);

                    String content = "";
                    try {
                        content = f.getLastChild().getTextContent().trim();
                    } catch (NullPointerException npe) {
                        continue;
                    }

                    String s = f.getNodeName();
                    if (s.equals("LastTradePriceOnly")) {
                        eqv = Double.parseDouble(content);
					}
                    else if (s.equals("Name")) {
                        name = content;
					}
					else if (s.equals("StockExchange")) {
						sx.add(content);
					}
                }
				
                Equity e = new Equity(Equity.Type.STOCK, sym, name, 0, eqv, sx);
                temp_list.add(e);
            }
        }

        if (equities_list.isEmpty()) {
            equities_list = temp_list;
            return;
        }

        for (Equity en : temp_list) {
            boolean found_it = false;
            for (Equity eo : equities_list) {
                if (eo.getTickerSymbol().equals(en.getTickerSymbol())) {
                    eo.setPrice_per_share(en.getPrice_per_share());
                    found_it = true;
                }
            }
            if (!found_it) {
                equities_list.add(en);
            }
        }

        for (Portfolio p : holding_list.keySet()) {
            for (Holding h : holding_list.get(p)) {
                if (h instanceof Equity) {
                    Equity e = (Equity) h;
                    e.setPrice_per_share(get_by_ticker(e.getTickerSymbol()).getPrice_per_share());
                }
            }
        }
    }

    /**
     * Loads the given string into an xml format to be parsed.
     *
     * @param xml the string to parse
     * @return The xml document
     */
    private static Document loadXMLFromString(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            return builder.parse(is);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Links the holdings to this manager.
     *
     * @param p The portfolio.
     */
    public static void link_holdings(Portfolio p) {
        holding_list.put(p, p.getHoldings());
    }

    /**
     * Gets an equity by the ticker name.
     *
     * @param ticker The ticker.
     * @return The equity.
     */
    public static Equity get_by_ticker(String ticker) {
        for (Equity e : equities_list) {
            if (e.getTickerSymbol().equals(ticker)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Searches for an equity.
     *
     * @param input      The input.
     * @param field_name The field name.
     * @param p          The portfolio.
     * @return An list of equities.
     * @throws IllegalAccessException If an access error occurs.
     */
    public static ArrayList<Holding> search(String input, String field_name, Portfolio p) throws IllegalAccessException {
        try {
            ArrayList<Holding> out = new ArrayList<Holding>();
            for (Holding h : holding_list.get(p)) {
                ArrayList<Field> fields = null;
                if (h instanceof Equity) {
                    fields = new ArrayList<Field>(Arrays.asList(((Equity) h).getClass().getFields()));
                } else if (h instanceof Account) {
                    fields = new ArrayList<Field>(Arrays.asList(((Account) h).getClass().getFields()));
                } else {
                    fields = new ArrayList<Field>(Arrays.asList(h.getClass().getFields()));
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

    /**
     * Searches for an equity in all portfolios.
     *
     * @param input      The input.
     * @param field_name The field name.
     * @return An list of equities.
     * @throws UnimportedEquitiesException If an access error occurs.
     */
    public static ArrayList<Equity> searchAll(String input, String field_name) throws UnimportedEquitiesException {
        if (equities_list == null) {
            throw new UnimportedEquitiesException(
                    "Please make sure that the Equities are imported before calling this function.");
        }
        ArrayList<Equity> out = new ArrayList<Equity>();
        for (Holding h : equities_list) {
            if (h instanceof Equity && h.match(input)) {
                out.add((Equity) h);
            }
        }
        return out;
    }

    public static class UnimportedEquitiesException extends Exception {
        public UnimportedEquitiesException(String message) {
            super(message);
        }
    }

    //returns the DJIA
    public static double djia() {
        double sum = 0;
        double div = 0.16416809180007;
        for (Equity e : equities_list) {
            if (djiaTickers.contains(e.getTickerSymbol())) {
                sum += e.getPrice_per_share();
            }
        }
        return Math.round((sum / div) * 100.0) / 100.0;
    }
}