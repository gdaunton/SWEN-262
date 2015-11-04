package main.model.simulation;

public class Bull extends Simulation {
    /**
     * Simulates a bull market.
     */
    @Override
    public double simulate(int nSteps, STEP_SIZE step_size, double start_val, double change) {
        switch (step_size) {
            case DAY:
                return start_val + (start_val * (change/100) * (1 / 365));
            case MONTH:
                return start_val + (start_val * (change/100) * (1 / 12));
            case YEAR:
                return start_val + (start_val * (change/100));
        }
        return start_val;
    }
}
