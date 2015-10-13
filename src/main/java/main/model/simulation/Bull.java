package main.model.simulation;

public class Bull extends Simulation{

    @Override
    public double simulate(int nSteps, STEP_SIZE step_size, double start_val) {
		switch(step_size) {
			DAY:
					return start_val * (1.0 + change) * (1 / 365);
			MONTH:
					return start_val * (1.0 + change) * (1 / 12);
			YEAR:
					return start_val * (1.0 + change);
		}
    }
}
