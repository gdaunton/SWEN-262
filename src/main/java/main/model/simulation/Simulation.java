package main.model.simulation;

import main.model.Portfolio;
import main.model.holdings.Equity;
import main.model.holdings.Holding;

public abstract class Simulation {

    double change = 0;

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
    public abstract double simulate(int nSteps, STEP_SIZE step_size, double start_val);

    /**
     * Performs a simulation on a portfolio.
     *
     * @param nSteps    Steps to take.
     * @param step_size Size of each step.
     * @param p         The portfolio.
     * @return The portfolio with the simulated values.
     */
    public Portfolio simulate(int nSteps, STEP_SIZE step_size, Portfolio p) {
        Portfolio out = new Portfolio(p.getUsers(), p.name);
        for (Holding h : p.getHoldings()) {
            out.addHolding(h);
        }

        double ne = p.eval_equities();
        ne = simulate(nSteps, step_size, ne);
        double ac = p.eval_accounts();

        for (Holding h : out.getHoldings()) {
            if (h instanceof Equity) {
                ((Equity) h).setPrice_per_share(simulate(nSteps, step_size, ((Equity) h).getPrice_per_share()));
            }
        }

        return out;
    }
}
