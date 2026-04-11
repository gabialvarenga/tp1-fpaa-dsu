package tp1.fpaa.experiment;

import tp1.fpaa.algorithm.dsu.DSU;
import tp1.fpaa.algorithm.dsu.DSUFullTarjan;
import tp1.fpaa.algorithm.dsu.DSUNaive;
import tp1.fpaa.algorithm.dsu.DSUUnionRank;
import tp1.fpaa.algorithm.mst.Edge;
import tp1.fpaa.algorithm.mst.MSTKruskal;
import tp1.fpaa.algorithm.mst.MSTResult;
import tp1.fpaa.statistics.ExperimentMetricsAggregator;

import java.util.Arrays;
import java.util.Random;

public class MSTBenchmarkRunner {

    private final int repetitions;
    private final long seed;
    private final int queryMultiplier;

    public MSTBenchmarkRunner(int repetitions, long seed, int queryMultiplier) {
        this.repetitions = repetitions;
        this.seed = seed;
        this.queryMultiplier = queryMultiplier;
    }

    public MSTBenchmarkResult runKruskal(String variant, int n, Edge[] edges) {
        ExperimentMetricsAggregator[] collectors = new ExperimentMetricsAggregator[repetitions];
        Edge[] sortedEdges = sortEdges(edges);

        for (int r = 0; r < repetitions; r++) {
            DSU dsu = createDSU(variant, n);
            ExperimentMetricsAggregator mc = new ExperimentMetricsAggregator();
            MSTKruskal kruskal = new MSTKruskal(dsu);

            kruskal.init(n);
            dsu.enableMetrics(mc);
            mc.startTimer();
            MSTResult mst = kruskal.computeOnSortedEdges(n, sortedEdges);
            mc.stopTimer();

            collectors[r] = mc;

            if (r == 0) {
                validateMST(variant, n, mst);
            }
        }

        return new MSTBenchmarkResult(variant, n, collectors);
    }

    public MSTBenchmarkResult runStress(String variant, int n, Edge[] edges) {
        ExperimentMetricsAggregator[] collectors = new ExperimentMetricsAggregator[repetitions];
        Edge[] sortedEdges = sortEdges(edges);

        int numQueries = queryMultiplier * n;
        int[] queryTargets = preGenerateQueryTargets(n, numQueries);

        for (int r = 0; r < repetitions; r++) {
            DSU dsu = createDSU(variant, n);
            ExperimentMetricsAggregator mc = new ExperimentMetricsAggregator();
            MSTKruskal kruskal = new MSTKruskal(dsu);

            kruskal.init(n);
            dsu.enableMetrics(mc);
            mc.startTimer();

            kruskal.computeOnSortedEdges(n, sortedEdges);

            for (int q = 0; q < numQueries; q++) {
                dsu.findSet(queryTargets[q]);
            }

            mc.stopTimer();
            collectors[r] = mc;
        }

        return new MSTBenchmarkResult(variant, n, collectors);
    }

    private DSU createDSU(String name, int n) {
        switch (name) {
            case "Naive":
                return new DSUNaive(n);
            case "UnionRank":
                return new DSUUnionRank(n);
            case "FullTarjan":
                return new DSUFullTarjan(n);
            default:
                throw new IllegalArgumentException("Variante desconhecida: " + name);
        }
    }

    private int[] preGenerateQueryTargets(int n, int numQueries) {
        Random rng = new Random(seed + n);
        int[] targets = new int[numQueries];
        for (int i = 0; i < numQueries; i++) {
            targets[i] = rng.nextInt(n);
        }
        return targets;
    }

    private void validateMST(String variant, int n, MSTResult result) {
        if (result.edgeCount() != n - 1) {
            System.out.printf("[AVISO] %s n=%d: AGM com %d arestas (esperado %d)%n",
                    variant, n, result.edgeCount(), n - 1);
        }
        if (result.getTotalCost() <= 0) {
            System.out.printf("[AVISO] %s n=%d: custo total invalido (%d)%n",
                    variant, n, result.getTotalCost());
        }
    }

    private Edge[] sortEdges(Edge[] edges) {
        if (edges == null) {
            throw new IllegalArgumentException("edges nao pode ser nulo.");
        }
        Edge[] sorted = edges.clone();
        Arrays.sort(sorted);
        return sorted;
    }
}
