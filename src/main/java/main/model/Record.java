package main.model;

import main.model.holdings.Holding;

import java.util.Calendar;
import java.util.Date;

public class Record {
	Date date;
	Holding h1, h2;
	double amount;
	Portfolio p;
	
	public enum Type {
		ACCOUNT_TRANSFER,
		ACCOUNT_DEPOSIT,
		ACCOUNT_WITHDRAW,
		EQUITY_BUY_SELL
	}
	
	public Record(Type type, Object[] args) {
		date = Calendar.getInstance().getTime();
		switch(type) {
			case ACCOUNT_DEPOSIT:
				h1 = (Holding) args[0];
				amount = (Double) args[1];
				break;
			case ACCOUNT_TRANSFER:
				h1 = (Holding)args[0];
				h2 = (Holding)args[1];
				amount = (Double)args[2];
				break;
			case ACCOUNT_WITHDRAW:
				h1 = (Holding)args[0];
				amount = (Double)args[1];
				break;
			case EQUITY_BUY_SELL:
				h1 = (Holding)args[0];
				amount = (Double)args[1];
				break;
		}
	}
}