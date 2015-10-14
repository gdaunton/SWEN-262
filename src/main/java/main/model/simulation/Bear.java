package main.model.simulation;

public class Bear extends Simulation{

    @Override
    public double simulate(int nSteps, STEP_SIZE step_size, double start_val) {
		switch(step_size) {
			case DAY:
				return start_val * (1.0 - change) * (1 / 365);
			case MONTH:
				return start_val * (1.0 - change) * (1 / 12);
			case YEAR:
				return start_val * (1.0 - change);
		}
		return 0;
    }
}
