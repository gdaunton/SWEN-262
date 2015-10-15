package main.model.holdings;

import java.io.Serializable;

public abstract class Holding implements Serializable {
	public abstract double getValue();

	@Override
	public boolean equals(Object p) {
		Holding h = (Holding) p;
		try {
			return h.getValue() == this.getValue();
		} catch (Exception e) {
			return false;
		}
	}
}