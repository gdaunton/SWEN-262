package main.model;

public class Record {
	Date date;
	Holding h1, h2;
	double amount;
	Portfolio p;
	
	enum Type {
		ACCOUNT_TRANSFER,
		ACCOUNT_DEPOSIT,
		ACCOUNT_WITHDRAW,
		EQUITY_BUY_SELL
	}
	
	public Record(Type type, Object[] args) {
		date = Calendar.getInstance().getTime();
		switch(type) {
			ACCOUNT_DEPOSIT:
								h1 = (Holding)args[0];
								amount = (Double)args[1];
								break;
			ACCOUNT_TRANSFER:
								h1 = (Holding)args[0];
								h2 = (Holding)args[1];
								amount = (Double)args[2];
								break;
			ACCOUNT_WITHDRAW:
								h1 = (Holding)args[0];
								amount = (Double)args[1];
								break;
			EQUITY_BUY:
								h1 = (Holding)args[0];
								amount = (Double)args[1];
								break;
			EQUITY_SELL:
								h1 = (Holding)args[0];
								amount = (Double)args[1];
								break;
		}
	}
	
	public String toString() {
		
	}
	
	public String h1ToString() {
		if(h1 != null) { return h1.toString(); }
		return "";
	}
	
	public String h2ToString() {
		if(h2 != null) { return h2.toString(); }
		return "";
	}
}