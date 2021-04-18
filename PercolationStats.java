/* *****************************************************************************
 *  Name:    Ada Lovelace
 *  NetID:   alovelace
 *  Precept: P00
 *
 *  Description:  Prints 'Hello, World' to the terminal window.
 *                By tradition, this is everyone's first program.
 *                Prof. Brian Kernighan initiated this tradition in 1974.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;


public class PercolationStats
{
    private final Percolation percolation;
    private final int trials;
    private double[] results;
    private final int gridSize;
    private static final double CONFIDENCE_95 = 1.96;

    public PercolationStats(int n, int trials)
    {
        this.percolation = new Percolation(n);
        this.gridSize = n * n;
        this.trials = trials;
        this.results = new double[trials];
        this.executePercolate(n);
    }

    private void executePercolate(int n)
    {
        for (int i = 0; i < this.trials; i++)
        {
            while (!this.percolation.percolates())
            {
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);
                this.percolation.open(row, col);
            }
            double res = (double) this.percolation.numberOfOpenSites() / this.gridSize;
            this.results[i] = res;
        }
    }

    public double mean()
    {
        return StdStats.mean(this.results);
    }

    public double stddev()
    {
        return StdStats.stddev(this.results);
    }

    public double confidenceLo()
    {
        return mean() - ((CONFIDENCE_95 * stddev()) / Math.sqrt(trials));
    }

    public double confidenceHi()
    {
        return this.mean() + ((CONFIDENCE_95 * stddev()) / Math.sqrt(trials));
    }


    public static void main(String[] args)
    {
        int n = Integer.parseInt(args[0]);
        int trial = Integer.parseInt(args[1]);
        PercolationStats stats = new PercolationStats(n, trial);
        String hiLow = "[" + stats.confidenceLo() + ", " + stats.confidenceLo() + "]";
        StdOut.println("mean\t\t\t\t\t = " + stats.mean());
        StdOut.println("stddev\t\t\t\t\t = " + stats.stddev());
        StdOut.println("95% confidence interval  = " + hiLow);

    }
}
