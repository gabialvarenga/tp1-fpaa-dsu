package tp1.fpaa.experiment.dsuCase.runner;

import tp1.fpaa.algorithm.dsu.*;
import tp1.fpaa.experiment.dsuCase.DSUExperimentResult;
import tp1.fpaa.experiment.dsuCase.DSUTreeBuilder;
import tp1.fpaa.experiment.dsuCase.DSUExperimentResult.PassResult;
import tp1.fpaa.statistics.ExperimentMetricsAggregator;

import java.util.Arrays;
import java.util.Random;

public final class E4Runner {

    private static final int E4_PASSES = 3;
    private static final long SEED = 42L;
    private static final String[] VARIANTS = { "UnionRank", "FullTarjan" };

    private E4Runner() {
    }

    protected static DSUExperimentResult[] run(int[] sizes) {

        DSUExperimentResult[] results = new DSUExperimentResult[VARIANTS.length * sizes.length];
        int idx = 0;

        Random rng = new Random(SEED);

        for (int n : sizes) {
            int[][] passOrders = generatePassOrders(n, rng);

            for (String variant : VARIANTS) {
                DSU dsu = createPreparedDSU(variant, n);

                PassResult[] passResults = runPasses(dsu, passOrders, n);

                results[idx++] = DSUExperimentResult.forE4(variant, n, passResults);
            }
        }

        return Arrays.copyOf(results, idx);
    }

    private static int[][] generatePassOrders(int n, Random rng) {
        int[][] orders = new int[E4_PASSES][n];

        for (int p = 0; p < E4_PASSES; p++) {
            int[] order = createSequentialArray(n);
            shuffle(order, rng);
            orders[p] = order;
        }

        return orders;
    }

    private static int[] createSequentialArray(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i;
        }
        return arr;
    }

    private static DSU createPreparedDSU(String variant, int n) {
        DSU dsu = createDSU(variant, n);

        initializeDSU(dsu, n);
        DSUTreeBuilder.buildMaxHeightTree(dsu, n);

        return dsu;
    }

    private static void initializeDSU(DSU dsu, int n) {
        for (int i = 0; i < n; i++) {
            dsu.makeSet(i);
        }
    }

    private static PassResult[] runPasses(DSU dsu, int[][] passOrders, int n) {
        PassResult[] results = new PassResult[E4_PASSES];

        for (int p = 0; p < E4_PASSES; p++) {
            results[p] = runSinglePass(dsu, passOrders[p], n, p);
        }

        return results;
    }

    private static PassResult runSinglePass(DSU dsu, int[] order, int n, int passIndex) {
        ExperimentMetricsAggregator mc = setupMetrics(dsu);

        mc.startTimer();

        for (int x : order) {
            dsu.findSet(x);
        }

        mc.stopTimer();

        double passMs = nanoToMs(mc.getTotalNano());
        long pointers = mc.getParentAccesses();
        double avgPathLen = calculateAvgPathLength(pointers, n);

        dsu.enableMetrics(null);
        int maxDepth = measureMaxDepth(dsu, n);

        return new PassResult(passIndex + 1, passMs, pointers, avgPathLen, maxDepth);
    }

    private static ExperimentMetricsAggregator setupMetrics(DSU dsu) {
        ExperimentMetricsAggregator mc = new ExperimentMetricsAggregator();
        dsu.enableMetrics(mc);
        return mc;
    }

    private static int measureMaxDepth(DSU dsu, int n) {
        int maxDepth = 0;

        if (dsu instanceof DSUUnionRank) {
            DSUUnionRank ur = (DSUUnionRank) dsu;
            for (int i = 0; i < n; i++) {
                maxDepth = Math.max(maxDepth, ur.depth(i));
            }
        } else if (dsu instanceof DSUFullTarjan) {
            DSUFullTarjan ft = (DSUFullTarjan) dsu;
            for (int i = 0; i < n; i++) {
                maxDepth = Math.max(maxDepth, ft.depth(i));
            }
        }

        return maxDepth;
    }

    private static void shuffle(int[] arr, Random rng) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }

    private static double calculateAvgPathLength(long pointers, int totalOps) {
        return (double) pointers / totalOps;
    }

    private static double nanoToMs(long nano) {
        return nano / 1_000_000.0;
    }

    private static DSU createDSU(String variant, int n) {
        switch (variant) {
            case "UnionRank":
                return new DSUUnionRank(n);
            case "FullTarjan":
                return new DSUFullTarjan(n);
            default:
                throw new IllegalArgumentException("Variante desconhecida: " + variant);
        }
    }
}