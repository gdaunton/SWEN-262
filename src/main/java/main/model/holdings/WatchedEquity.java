package main.model.holdings;

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

	private String symbol = "";
	public double lowTrigger = -1;
	public double highTrigger = -1;
	public List<TriggerNode> triggerNodes = new ArrayList<>();

	public WatchedEquity(String symbol) {
		this.symbol = symbol;
		HoldingManager.get_by_ticker(symbol).addObserver(this);
	}

	public String getSymbol() {
		return this.symbol;
	}

	public boolean isTriggered() {
		double ppshare = HoldingManager.get_by_ticker(symbol).getPrice_per_share();
		return ppshare < lowTrigger || ppshare > highTrigger;
	}

	public void update(Observable observable, Object object) {
		if(observable instanceof Equity) {
			double ppshare = (double) object;
			TriggerNode current = triggerNodes.get(0);
			if (lowTrigger != -1 && ppshare < lowTrigger) {
				if(current.type == TriggerNode.Type.HIGH)
					current.endStamp = Calendar.getInstance().getTime();
				if(current.endStamp != null)
					triggerNodes.add(new TriggerNode(TriggerNode.Type.LOW));
			}
			else if (highTrigger != -1 && ppshare > highTrigger) {
				if(current.type == TriggerNode.Type.LOW)
					current.endStamp = Calendar.getInstance().getTime();
				if(current.endStamp != null)
					triggerNodes.add(new TriggerNode(TriggerNode.Type.HIGH));
			}
			else {
				if(triggerNodes.get(0).endStamp == null)
					current.endStamp = Calendar.getInstance().getTime();
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		return o != null && this.symbol.equals(((WatchedEquity)o).symbol);
	}

	@Override
	public String toString() {
		return symbol + "  " + HoldingManager.get_by_ticker(symbol).getName();
	}
}

class TriggerNode {

	public enum Type {
		HIGH,
		LOW
	}

	public Type type;
	public Date timeStamp;
	public Date endStamp;

	public TriggerNode(Type type) {
		this.type = type;
		this.timeStamp = Calendar.getInstance().getTime();
	}
}
