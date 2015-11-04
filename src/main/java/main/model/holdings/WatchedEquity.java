package main.model.holdings;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableArray;

public class WatchedEquity implements Serializable, Observer {

	public enum Type {
		HIGH,
		LOW,
		NORMAL
	}

	private String symbol = "";
	public double lowTrigger = -1;
	public double highTrigger = -1;
	public transient List<TriggerNode> triggerNodes;

	/**
	 * Creates a new watch equity
	 * @param symbol the symbol of the equity to watch
	 */
	public WatchedEquity(String symbol) {
		this.symbol = symbol;
		triggerNodes = new ArrayList<>();
		HoldingManager.get_by_ticker(symbol).addObserver(this);
		update();
	}

	/**
	 * Get the symbol
	 * @return the symbol
	 */
	public String getSymbol() {
		return this.symbol;
	}

	/**
	 * If the equity is triggered
	 * @return if triggered
	 */
	public boolean isTriggered() {
		double ppshare = HoldingManager.get_by_ticker(symbol).getPrice_per_share();
		return ppshare < lowTrigger || ppshare > highTrigger;
	}

	@Override
	public void update(Observable o, Object arg) {
		double ppshare = (double) arg;
		if (lowTrigger != -1 && ppshare < lowTrigger)
			triggerNodes.add(new TriggerNode(Type.LOW, ppshare));
		else if (highTrigger != -1 && ppshare > highTrigger)
			triggerNodes.add(new TriggerNode(Type.HIGH, ppshare));
		else
			triggerNodes.add(new TriggerNode(Type.NORMAL, ppshare));
	}

	/**
	 * Update this watched equity
	 */
	private void update() {
		double ppshare = HoldingManager.get_by_ticker(symbol).getPrice_per_share();
		if (lowTrigger != -1 && ppshare < lowTrigger)
			triggerNodes.add(new TriggerNode(Type.LOW, ppshare));
		else if (highTrigger != -1 && ppshare > highTrigger)
			triggerNodes.add(new TriggerNode(Type.HIGH, ppshare));
		else
			triggerNodes.add(new TriggerNode(Type.NORMAL, ppshare));
	}

	/**
	 * Read the given object
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		HoldingManager.get_by_ticker(symbol).addObserver(this);
		triggerNodes = new ArrayList<>();
		update();
	}

	@Override
	public boolean equals(Object o) {
		return o != null && this.symbol.equals(((WatchedEquity)o).symbol);
	}

	@Override
	public String toString() {
		return symbol + "  " + HoldingManager.get_by_ticker(symbol).getName();
	}

	public class TriggerNode {

		public Type type;
		public Date timeStamp;
		public double ppshare;

		/**
		 * A new trigger node
		 * @param type the type
		 * @param ppshare the price per share
		 */
		public TriggerNode(Type type, double ppshare) {
			this.type = type;
			this.timeStamp = Calendar.getInstance().getTime();
			this.ppshare = ppshare;
		}

		@Override
		public String toString() {
			return timeStamp.toString() + "   " +  ppshare;
		}
	}
}
