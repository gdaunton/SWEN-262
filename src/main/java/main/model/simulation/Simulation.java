package main.model.simulation;

public abstract class Simulation {
	
	double change = 0;
	
	enum STEP_SIZE {
		DAY,
		MONTH,
		YEAR
	}

    public abstract double simulate(int nSteps, STEP_SIZE step_size, double start_val);
}
