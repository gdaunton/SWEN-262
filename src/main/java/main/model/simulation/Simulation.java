package main.model.simulation;

public abstract class Simulation {
	
	double change = 0;
	
	enum STEP_SIZE {
		DAY,
		MONTH,
		YEAR
	}

    public abstract double simulate(int nSteps, STEP_SIZE step_size, double start_val);
	
	public Portfolio simulate(int nSteps, STEP_SIZE step_size, Portfolio p) {
		Portfolio out = new Portfolio(p.getUsers(), p.name)
		for(Holding h : p.getHoldings()) { out.addHolding(h); }
		
		double ne = p.eval_equities();
		ne = simulate(nSteps, step_size, ne);
		double ac = p.eval_accounts();
		
		for(Holding h : out.getHoldings()) {
			if(h instanceof Equity) { ((Equity)h).setPrice_per_share(simulate(nSteps, step_size, ((Equity)h).getPrice_per_Share())); }
		}
		
		return out;
	}
}
