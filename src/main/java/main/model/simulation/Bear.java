package main.model.simulation;

public class Bear extends Simulation {

    /**
     * Simulates a bear market.
     */
    @Override
    public double simulate(int nSteps, STEP_SIZE step_size, double start_val, double change) {
        switch (step_size) {
            case DAY:
                return Math.round((start_val - (start_val * (change/100.0) * (1.0 / 365.0))) * 100.0) / 100.0;
            case MONTH:
                return Math.round((start_val - (start_val * (change/100.0) * (1.0 / 12.0))) * 100.0) / 100.0;
            case YEAR:
                return Math.round((start_val - (start_val * (change/100.0))) * 100.0) / 100.0;
        }
        return start_val;
    }
}
