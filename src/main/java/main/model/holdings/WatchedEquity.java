package main.model.holdings;

import java.math.BigDecimal;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WatchedEquity {

	private final StringProperty symbol = new SimpleStringProperty();
	private final ObjectProperty<BigDecimal> lowTrigger = new SimpleObjectProperty<BigDecimal>();
	private final ObjectProperty<BigDecimal> highTrigger = new SimpleObjectProperty<BigDecimal>();

	private final BooleanProperty triggered = new SimpleBooleanProperty();

	public String getSymbol() {
		return this.symbol.get();
	}

	public void setSymbol(String symbol) {
		this.symbol.set(symbol);
	}

	public StringProperty symbolProperty() {
		return this.symbol;
	}

	public BigDecimal getLowTrigger() {
		return this.lowTrigger.get();
	}

	public void setLowTrigger(BigDecimal lowTrigger) {
		this.lowTrigger.set(lowTrigger);
	}

	public ObjectProperty<BigDecimal> lowTriggerProperty() {
		return this.lowTrigger;
	}

	public BigDecimal getHighTrigger() {
		return this.highTrigger.get();
	}

	public void setHighTrigger(BigDecimal highTrigger) {
		this.highTrigger.set(highTrigger);
	}

	public ObjectProperty<BigDecimal> highTriggerProperty() {
		return this.highTrigger;
	}

	public boolean getTriggered() {
		return this.triggered.get();
	}

	public void setTriggered(boolean triggered) {
		this.triggered.set(triggered);
	}

	public BooleanProperty triggeredProperty() {
		return this.triggered;
	}

	public void trigger(String price) {
		BigDecimal actual = new BigDecimal(price);

		if (actual.compareTo(lowTrigger.get()) == -1 || actual.compareTo(highTrigger.get()) == 1)
			setTriggered(true);
		else
			setTriggered(false);
	}
}
