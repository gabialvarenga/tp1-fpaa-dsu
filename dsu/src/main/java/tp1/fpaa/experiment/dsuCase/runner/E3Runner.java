package tp1.fpaa.experiment.dsuCase.runner;

import tp1.fpaa.algorithm.dsu.*;
import tp1.fpaa.experiment.dsuCase.DSUExperimentResult;
import tp1.fpaa.statistics.DescriptiveStatisticsCalculator;
import tp1.fpaa.statistics.ExperimentMetricsAggregator;

import java.util.Arrays;
import java.util.Random;

public final class E3Runner {

    private static final int E3_REPETITIONS = 5;
    private static final long E3_SEED = 42L;
    private static final int MAX_NAIVE = 100_000;
    private static final String[] VARIANTS = { "Naive", "UnionRank", "FullTarjan" };

    private E3Runner() {
    }

    protected static DSUExperimentResult[] run(int[] sizes) {

        DSUExperimentResult[] results = new DSUExperimentResult[calculateTotalSlots(sizes, VARIANTS)];
        int idx = 0;

        long[] naiveMedianBySize = new long[sizes.length];
        long[] unionRankMedianBySize = new long[sizes.length];

        for (int si = 0; si < sizes.length; si++) {
            int n = sizes[si];
            int m = calculateNumOperations(n);

            OperationBatch ops = generateOperations(n, m);

            for (String variant : VARIANTS) {
                if (shouldSkipVariant(variant, n))
                    continue;

                ExperimentMetricsAggregator[] aggregators = runBenchmark(variant, n, ops);

                long medianNs = calculateMedian(aggregators);
                double nsPerOp = calculateNsPerOp(medianNs, m);

                if (isNaive(variant)) {
                    naiveMedianBySize[si] = medianNs;
                }

                if (isUnionRank(variant)) {
                    unionRankMedianBySize[si] = medianNs;
                }

                double speedupVsNaive = calculateSpeedupVsNaive(
                        variant, n, medianNs, naiveMedianBySize[si]);

                double speedupVsUnionRank = calculateSpeedupVsUnionRank(
                        variant, n, medianNs, unionRankMedianBySize[si]);

                results[idx++] = DSUExperimentResult.forE3(
                        variant,
                        n,
                        nanoToMs(medianNs),
                        nsPerOp,
                        speedupVsNaive,
                        speedupVsUnionRank);
            }
        }

        return Arrays.copyOf(results, idx);
    }

    private static int calculateTotalSlots(int[] sizes, String[] variants) {
        int total = 0;

        for (int n : sizes) {
            for (String v : variants) {
                if (!isNaive(v) || n <= MAX_NAIVE) {
                    total++;
                }
            }
        }

        return total;
    }

    private static int calculateNumOperations(int n) {
        return 5 * n;
    }

    private static class OperationBatch {
        int[] opA;
        int[] opB;
        boolean[] isUnion;
    }

    private static OperationBatch generateOperations(int n, int m) {
        Random rng = new Random(E3_SEED + n);

        OperationBatch ops = new OperationBatch();
        ops.opA = new int[m];
        ops.opB = new int[m];
        ops.isUnion = new boolean[m];

        for (int i = 0; i < m; i++) {
            ops.opA[i] = rng.nextInt(n);
            ops.opB[i] = rng.nextInt(n);
            ops.isUnion[i] = rng.nextDouble() < 0.6;
        }

        return ops;
    }

    private static ExperimentMetricsAggregator[] runBenchmark(
            String variant,
            int n,
            OperationBatch ops) {
        int m = ops.opA.length;
        ExperimentMetricsAggregator[] aggregators = new ExperimentMetricsAggregator[E3_REPETITIONS];

        for (int r = 0; r < E3_REPETITIONS; r++) {
            DSU dsu = createDSU(variant, n);
            initializeDSU(dsu, n);

            ExperimentMetricsAggregator mc = setupMetrics(dsu);

            mc.startTimer();
            executeOperations(dsu, ops, m);
            mc.stopTimer();

            aggregators[r] = mc;
        }

        return aggregators;
    }

    private static void initializeDSU(DSU dsu, int n) {
        for (int i = 0; i < n; i++) {
            dsu.makeSet(i);
        }
    }

    private static void executeOperations(DSU dsu, OperationBatch ops, int m) {
        for (int i = 0; i < m; i++) {
            if (ops.isUnion[i]) {
                dsu.union(ops.opA[i], ops.opB[i]);
            } else {
                dsu.findSet(ops.opA[i]);
            }
        }
    }

    private static ExperimentMetricsAggregator setupMetrics(DSU dsu) {
        ExperimentMetricsAggregator mc = new ExperimentMetricsAggregator();
        dsu.enableMetrics(mc);
        return mc;
    }

    private static long calculateMedian(ExperimentMetricsAggregator[] aggs) {
        return DescriptiveStatisticsCalculator.medianTimeNano(aggs);
    }

    private static double calculateNsPerOp(long medianNs, int m) {
        return (double) medianNs / m;
    }

    // Ganho relativo ao Naive (comportamento original)
    private static double calculateSpeedupVsNaive(String variant, int n, long current, long naive) {
        if (isNaive(variant) || naive <= 0 || n > MAX_NAIVE) {
            return Double.NaN;
        }
        return (double) naive / current;
    }

    // Ganho relativo ao UnionRank (nova métrica)
    // - UnionRank: sempre NaN (baseline de si mesmo)
    // - Naive: sempre NaN (mais lento que o baseline, não faz sentido como ganho)
    // - FullTarjan: razão unionRankMedian / fullTarjanMedian
    //   O valor unionRankMedianBySize[si] só estará preenchido quando FullTarjan
    //   for processado, pois a ordem do loop é Naive → UnionRank → FullTarjan.
    private static double calculateSpeedupVsUnionRank(String variant, int n,
            long current, long unionRankMedian) {
        if (!isFullTarjan(variant) || unionRankMedian <= 0) {
            return Double.NaN;
        }
        return (double) unionRankMedian / current;
    }

    private static boolean isNaive(String variant) {
        return variant.equals("Naive");
    }

    private static boolean isUnionRank(String variant) {
        return variant.equals("UnionRank");
    }

    private static boolean isFullTarjan(String variant) {
        return variant.equals("FullTarjan");
    }

    private static boolean shouldSkipVariant(String variant, int n) {
        return isNaive(variant) && n > MAX_NAIVE;
    }

    private static double nanoToMs(long nano) {
        return nano / 1_000_000.0;
    }

    private static DSU createDSU(String variant, int n) {
        switch (variant) {
            case "Naive":
                return new DSUNaive(n);
            case "UnionRank":
                return new DSUUnionRank(n);
            case "FullTarjan":
                return new DSUFullTarjan(n);
            default:
                throw new IllegalArgumentException("Variante desconhecida: " + variant);
        }
    }
}