package main.model.holdings;

import java.util.ArrayList;

public abstract class Holding implements Comparable {
    public abstract String[] as_table_row();
    public abstract int compareTo(Object o);
}