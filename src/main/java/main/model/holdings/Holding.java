package main.model.holdings;

import java.io.Serializable;
import java.util.Observable;

public abstract class Holding extends Observable implements Serializable {
    public abstract double getValue();

    public abstract String toString();

    @Override
    public boolean equals(Object p) {
        Holding h = (Holding) p;
        try {
            return h.getValue() == this.getValue();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Searches for a match with the given query. Use this for the search function.
     *
     * @param query The query to search for
     * @return Whether or not this holding matches the given query.
     */
    public abstract boolean match(String query);

    public abstract Holding clone();
}