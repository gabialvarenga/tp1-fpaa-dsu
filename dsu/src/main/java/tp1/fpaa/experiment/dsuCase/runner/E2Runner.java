package tp1.fpaa.experiment.dsuCase.runner;

import tp1.fpaa.algorithm.dsu.DSU;
import tp1.fpaa.algorithm.dsu.DSUUnionRank;
import tp1.fpaa.experiment.dsuCase.DSUExperimentResult;
import tp1.fpaa.experiment.dsuCase.DSUTreeBuilder;
import tp1.fpaa.statistics.ExperimentMetricsAggregator;

public final class E2Runner {

    private static final int E2_NUM_FINDS = 10_000;

    private E2Runner() {
    }

    protected static DSUExperimentResult[] run(int[] sizes) {
        DSUExperimentResult[] results = new DSUExperimentResult[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int n = sizes[i];

            DSUUnionRank dsu = createBalancedDSU(n);

            int deepest = findDeepestElement(dsu, n);
            int maxHeight = dsu.depth(deepest);
            int theoreticalMax = calculateTheoreticalHeight(n);

            ExperimentMetricsAggregator mc = setupMetrics(dsu);

            runFindExperiment(dsu, mc, deepest);

            results[i] = buildResult(n, mc, maxHeight, theoreticalMax);
        }

        return results;
    }

    private static DSUUnionRank createBalancedDSU(int n) {
        DSUUnionRank dsu = new DSUUnionRank(n);

        for (int i = 0; i < n; i++) {
            dsu.makeSet(i);
        }

        DSUTreeBuilder.buildMaxHeightTree(dsu, n);
        return dsu;
    }

    private static ExperimentMetricsAggregator setupMetrics(DSU dsu) {
        ExperimentMetricsAggregator mc = new ExperimentMetricsAggregator();
        dsu.enableMetrics(mc);
        return mc;
    }

    private static void runFindExperiment(DSUUnionRank dsu, ExperimentMetricsAggregator mc, int target) {
        mc.startTimer();

        for (int i = 0; i < E2_NUM_FINDS; i++) {
            dsu.findSet(target);
        }

        mc.stopTimer();
    }

    private static int calculateTheoreticalHeight(int n) {
        return (int) Math.floor(Math.log(n) / Math.log(2));
    }

    private static DSUExperimentResult buildResult(int n,
            ExperimentMetricsAggregator mc,
            int maxHeight,
            int theoreticalMax) {

        long pointers = mc.getParentAccesses();
        double avgPathLen = calculateAvgPathLength(pointers, E2_NUM_FINDS);

        return DSUExperimentResult.forE2(n, pointers, avgPathLen, maxHeight, theoreticalMax);
    }

    private static int findDeepestElement(DSUUnionRank dsu, int n) {
        int deepest = 0;
        int maxDepth = 0;

        for (int i = 0; i < n; i++) {
            int depth = dsu.depth(i);

            if (depth > maxDepth) {
                maxDepth = depth;
                deepest = i;
            }
        }

        return deepest;
    }

    private static double calculateAvgPathLength(long pointers, int totalOps) {
        return (double) pointers / totalOps;
    }
}