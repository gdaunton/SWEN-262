package main.model.simulation;

public abstract class Simulation {
	
	enum STEP_SIZE {
		DAY,
		MONTH,
		YEAR
	}

    public abstract void simulate(int nSteps, STEP_SIZE step_size, Portfolio p);
}
