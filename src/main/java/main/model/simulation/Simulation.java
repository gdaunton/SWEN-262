package main.model.simulation;

public abstract class Simulation {
	
	double change = 0;
	
	enum STEP_SIZE {
		DAY,
		MONTH,
		YEAR
	}

    public abstract void simulate(int nSteps, STEP_SIZE step_size, Portfolio p);
}
