package model.holdings

import java.util.ArrayList;

public abstract class Holding implements Comparable {
    abstract String[] as_table_row();
    abstract int compareTo(Object o);
}