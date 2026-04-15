package tp1.fpaa.experiment.dsuCase.runner;

import tp1.fpaa.algorithm.dsu.DSUNaive;
import tp1.fpaa.experiment.dsuCase.DSUExperimentResult;
import tp1.fpaa.statistics.ExperimentMetricsAggregator;

public final class E1Runner {

    private static final int E1_REPETITIONS = 10;

    private E1Runner() {
    }

    protected static DSUExperimentResult[] run(int[] sizes) {
        DSUExperimentResult[] results = new DSUExperimentResult[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int n = sizes[i];

            DSUNaive dsu = createWorstCaseDSU(n);
            ExperimentMetricsAggregator mc = setupMetrics(dsu);

            runFindExperiment(dsu, mc, n);

            results[i] = buildResult(n, mc);
        }

        return results;
    }

    private static DSUNaive createWorstCaseDSU(int n) {
        DSUNaive dsu = new DSUNaive(n);

        for (int i = 0; i < n; i++) {
            dsu.makeSet(i);
        }

        for (int i = 0; i < n - 1; i++) {
            dsu.union(i, i + 1);
        }

        return dsu;
    }

    private static ExperimentMetricsAggregator setupMetrics(DSUNaive dsu) {
        ExperimentMetricsAggregator mc = new ExperimentMetricsAggregator();
        dsu.enableMetrics(mc);
        return mc;
    }

    private static void runFindExperiment(DSUNaive dsu, ExperimentMetricsAggregator mc, int n) {
        mc.startTimer();

        for (int rep = 0; rep < E1_REPETITIONS; rep++) {
            for (int i = 0; i < n; i++) {
                dsu.findSet(i);
            }
        }

        mc.stopTimer();
    }

    private static DSUExperimentResult buildResult(int n, ExperimentMetricsAggregator mc) {
        long pointers = mc.getParentAccesses();
        double avgPathLen = calculateAvgPathLength(pointers, n * E1_REPETITIONS);

        return DSUExperimentResult.forE1(n, pointers, avgPathLen);
    }

    private static double calculateAvgPathLength(long pointers, int totalOps) {
        return (double) pointers / totalOps;
    }
}