package tp1.fpaa.experiment;

import tp1.fpaa.algorithm.dsu.DSU;
import tp1.fpaa.algorithm.dsu.DSUFullTarjan;
import tp1.fpaa.algorithm.dsu.DSUNaive;
import tp1.fpaa.algorithm.dsu.DSUUnionRank;
import tp1.fpaa.experiment.DSUExperimentResult.PassResult;
import tp1.fpaa.statistics.ExperimentMetricsAggregator;

import java.util.Arrays;
import java.util.Random;

public final class DSUCaseRunner {

    private static final int E3_REPETITIONS = 5;
    private static final long E3_SEED = 42L;
    private static final int E4_PASSES = 3;

    private DSUCaseRunner() {
    }

    public static DSUExperimentResult[] runE1(int[] sizes) {
        DSUExperimentResult[] results = new DSUExperimentResult[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int n = sizes[i];

            DSUNaive dsu = new DSUNaive(n);
            for (int x = 0; x < n; x++)
                dsu.makeSet(x);

            for (int x = 0; x < n - 1; x++)
                dsu.union(x, x + 1);

            ExperimentMetricsAggregator mc = new ExperimentMetricsAggregator();
            dsu.enableMetrics(mc);

            int repetitions = 10;
            mc.startTimer();
            for (int rep = 0; rep < repetitions; rep++) {
                for (int f = 0; f < n; f++)
                    dsu.findSet(f);
            }
            mc.stopTimer();

            long pointers = mc.getParentAccesses();
            double avgPathLen = (double) pointers / (n * repetitions);

            results[i] = DSUExperimentResult.forE1(n, pointers, avgPathLen);
        }
        return results;
    }

    public static DSUExperimentResult[] runE2(int[] sizes) {
        DSUExperimentResult[] results = new DSUExperimentResult[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int n = sizes[i];

            DSUUnionRank dsu = new DSUUnionRank(n);
            for (int x = 0; x < n; x++)
                dsu.makeSet(x);

            int[] roots = new int[n];
            for (int x = 0; x < n; x++)
                roots[x] = x;
            int activeRoots = n;

            while (activeRoots > 1) {
                int newSize = (activeRoots + 1) / 2;
                int[] nextRoots = new int[newSize];
                int idx = 0;
                for (int j = 0; j < activeRoots / 2; j++) {
                    dsu.union(roots[2 * j], roots[2 * j + 1]);
                    nextRoots[idx++] = dsu.findSet(roots[2 * j]);
                }
                if (activeRoots % 2 == 1) {
                    nextRoots[idx] = roots[activeRoots - 1];
                }
                activeRoots = newSize;
                roots = nextRoots;
            }

            int deepest = findDeepestElement(dsu, n);
            int maxHeight = dsu.depth(deepest);
            int theoreticalMax = (int) Math.floor(Math.log(n) / Math.log(2));

            int numFinds = 10_000;
            ExperimentMetricsAggregator mc = new ExperimentMetricsAggregator();
            dsu.enableMetrics(mc);
            mc.startTimer();
            for (int f = 0; f < numFinds; f++)
                dsu.findSet(deepest);
            mc.stopTimer();

            long pointers = mc.getParentAccesses();
            double avgPathLen = (double) pointers / numFinds;

            results[i] = DSUExperimentResult.forE2(n, pointers, avgPathLen, maxHeight, theoreticalMax);
        }
        return results;
    }

    public static DSUExperimentResult[] runE3(int[] sizes) {
        String[] variants = { "Naive", "UnionRank", "FullTarjan" };
        int maxNaive = 50_000;

        int totalSlots = 0;
        for (int n : sizes) {
            for (String v : variants) {
                if (!v.equals("Naive") || n <= maxNaive)
                    totalSlots++;
            }
        }
        DSUExperimentResult[] results = new DSUExperimentResult[totalSlots];
        int idx = 0;

        long[] naiveMedianBySize = new long[sizes.length];

        for (int si = 0; si < sizes.length; si++) {
            int n = sizes[si];
            int m = 5 * n;

            Random rng = new Random(E3_SEED + n);
            int[] opA = new int[m];
            int[] opB = new int[m];
            boolean[] isUnion = new boolean[m];
            for (int k = 0; k < m; k++) {
                opA[k] = rng.nextInt(n);
                opB[k] = rng.nextInt(n);
                isUnion[k] = rng.nextDouble() < 0.6;
            }

            for (String variant : variants) {
                if (variant.equals("Naive") && n > maxNaive)
                    continue;

                long[] times = new long[E3_REPETITIONS];

                for (int r = 0; r < E3_REPETITIONS; r++) {
                    DSU dsu = createDSU(variant, n);
                    for (int x = 0; x < n; x++)
                        dsu.makeSet(x);

                    ExperimentMetricsAggregator mc = new ExperimentMetricsAggregator();
                    dsu.enableMetrics(mc);
                    mc.startTimer();
                    for (int k = 0; k < m; k++) {
                        if (isUnion[k])
                            dsu.union(opA[k], opB[k]);
                        else
                            dsu.findSet(opA[k]);
                    }
                    mc.stopTimer();
                    times[r] = mc.getTotalNano();
                }

                long medianNs = median(times);
                long medianMs = medianNs / 1_000_000;
                double nsPerOp = (double) medianNs / m;

                if (variant.equals("Naive")) {
                    naiveMedianBySize[si] = medianNs;
                }

                double speedup = Double.NaN;
                if (!variant.equals("Naive") && naiveMedianBySize[si] > 0
                        && n <= maxNaive) {
                    speedup = (double) naiveMedianBySize[si] / medianNs;
                }

                results[idx++] = DSUExperimentResult.forE3(variant, n, medianMs, nsPerOp, speedup);
            }
        }

        return Arrays.copyOf(results, idx);
    }

    public static DSUExperimentResult[] runE4(int[] sizes) {
        DSUExperimentResult[] results = new DSUExperimentResult[2 * sizes.length];
        int idx = 0;

        Random rng = new Random(E3_SEED);

        for (int n : sizes) {
            int[][] passOrders = new int[E4_PASSES][n];
            for (int p = 0; p < E4_PASSES; p++) {
                int[] order = new int[n];
                for (int x = 0; x < n; x++)
                    order[x] = x;
                shuffleArray(order, rng);
                passOrders[p] = order;
            }

            String[] variants = { "UnionRank", "FullTarjan" };
            for (String variant : variants) {
                DSU dsu = createDSU(variant, n);
                for (int x = 0; x < n; x++)
                    dsu.makeSet(x);
                buildMaxHeightTree(dsu, n);

                PassResult[] passResults = new PassResult[E4_PASSES];

                for (int p = 0; p < E4_PASSES; p++) {
                    ExperimentMetricsAggregator mc = new ExperimentMetricsAggregator();
                    dsu.enableMetrics(mc);
                    mc.startTimer();
                    for (int x : passOrders[p])
                        dsu.findSet(x);
                    mc.stopTimer();

                    long passMs = mc.getTotalNano() / 1_000_000;
                    long pointers = mc.getParentAccesses();
                    double avgPathLen = (double) pointers / n;

                    dsu.enableMetrics(null);
                    int maxDepth = measureMaxDepth(dsu, n);

                    passResults[p] = new PassResult(p + 1, passMs, pointers, avgPathLen, maxDepth);
                }

                results[idx++] = DSUExperimentResult.forE4(variant, n, passResults);
            }
        }

        return Arrays.copyOf(results, idx);
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

    private static void buildMaxHeightTree(DSU dsu, int n) {
        int[] roots = new int[n];
        for (int x = 0; x < n; x++)
            roots[x] = x;
        int activeRoots = n;

        while (activeRoots > 1) {
            int newSize = (activeRoots + 1) / 2;
            int[] nextRoots = new int[newSize];
            int idx = 0;
            for (int j = 0; j < activeRoots / 2; j++) {
                dsu.union(roots[2 * j], roots[2 * j + 1]);
                nextRoots[idx++] = dsu.findSet(roots[2 * j]);
            }
            if (activeRoots % 2 == 1) {
                nextRoots[idx] = roots[activeRoots - 1];
            }
            activeRoots = newSize;
            roots = nextRoots;
        }
    }

    private static int measureMaxDepth(DSU dsu, int n) {
        int maxDepth = 0;
        if (dsu instanceof DSUUnionRank) {
            DSUUnionRank ur = (DSUUnionRank) dsu;
            for (int x = 0; x < n; x++)
                maxDepth = Math.max(maxDepth, ur.depth(x));
        } else if (dsu instanceof DSUFullTarjan) {
            DSUFullTarjan ft = (DSUFullTarjan) dsu;
            for (int x = 0; x < n; x++)
                maxDepth = Math.max(maxDepth, ft.depth(x));
        }
        return maxDepth;
    }

    private static int findDeepestElement(DSUUnionRank dsu, int n) {
        int deepest = 0;
        int maxDepth = 0;
        for (int x = 0; x < n; x++) {
            int d = dsu.depth(x);
            if (d > maxDepth) {
                maxDepth = d;
                deepest = x;
            }
        }
        return deepest;
    }

    private static long median(long[] values) {
        long[] copy = values.clone();
        Arrays.sort(copy);
        int mid = copy.length / 2;
        return copy.length % 2 == 0
                ? (copy[mid - 1] + copy[mid]) / 2
                : copy[mid];
    }

    private static void shuffleArray(int[] arr, Random rng) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }
}