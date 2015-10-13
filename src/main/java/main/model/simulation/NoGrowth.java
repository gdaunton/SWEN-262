package main.model.simulation;


public class NoGrowth extends Simulation{

    @Override
    public double simulate(int nSteps, STEP_SIZE step_size, double start_val) { return start_val; }

}
