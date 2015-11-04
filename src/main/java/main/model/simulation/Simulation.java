package main.model.simulation;

import main.model.Portfolio;
import main.model.holdings.Equity;
import main.model.holdings.Holding;

public abstract class Simulation {

    public enum STEP_SIZE {
        DAY, MONTH, YEAR
    }

    /**
     * Simulates a market.
     *
     * @param nSteps    Steps to take.
     * @param step_size Size of each step.
     * @param start_val Start value.
     * @return The resulting value.
     */
    protected abstract double simulate(int nSteps, STEP_SIZE step_size, double start_val, double change);

    /**
     * Performs a simulation on a portfolio.
     *
     * @param nSteps    Steps to take.
     * @param step_size Size of each step.
     * @param p         The portfolio.
     * @return The portfolio with the simulated values.
     */
    public Portfolio simulate(int nSteps, STEP_SIZE step_size, Portfolio p, double decimal_change) {
        Portfolio out = new Portfolio(p.getUsers(), p.name);
        p.getHoldings().forEach(out::addHolding);

        for (Holding h : out.getHoldings()) {
            if (h instanceof Equity) {
                ((Equity) h).setPrice_per_share(simulate(nSteps, step_size, ((Equity) h).getPrice_per_share(), decimal_change));
            }
        }
        return out;
    }
}
