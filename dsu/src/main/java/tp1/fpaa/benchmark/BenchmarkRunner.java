package tp1.fpaa.benchmark;


import tp1.fpaa.algorithm.dsu.DSU;
import tp1.fpaa.algorithm.dsu.DSUFullTarjan;
import tp1.fpaa.algorithm.dsu.DSUNaive;
import tp1.fpaa.algorithm.dsu.DSUUnionRank;
import tp1.fpaa.algorithm.mst.Edge;
import tp1.fpaa.algorithm.mst.MSTKruskal;
import tp1.fpaa.algorithm.mst.MSTResult;
import tp1.fpaa.statistics.ExperimentMetricsAggregator;

import java.util.Random;

/**
 * Orquestra a execução dos experimentos comparativos entre variantes de DSU.
 *
 * Não conhece formatação, saída ou geração de grafos — recebe o grafo pronto
 * e devolve um {@link BenchmarkResult} com os dados brutos. Isso permite
 * testar a lógica do experimento de forma isolada e trocar a camada de
 * exibição sem alterar esta classe.
 */
public class BenchmarkRunner {

    private final int repetitions;
    private final long seed;
    private final int queryMultiplier;

    /**
     * @param repetitions     número de repetições por configuração
     * @param seed            semente usada para gerar os índices de query no stress
     * @param queryMultiplier fator multiplicador de n para o número de queries no stress
     */
    public BenchmarkRunner(int repetitions, long seed, int queryMultiplier) {
        this.repetitions = repetitions;
        this.seed = seed;
        this.queryMultiplier = queryMultiplier;
    }

    /**
     * O DSU é recriado a cada repetição. makeSet é executado fora da janela
     * de medição; apenas findSet e union são contabilizados.
     *
     * @param variant nome da variante (Naive, UnionRank ou FullTarjan)
     */
    public BenchmarkResult runKruskal(String variant, int n, Edge[] edges) {
        ExperimentMetricsAggregator[] collectors = new ExperimentMetricsAggregator[repetitions];

        for (int r = 0; r < repetitions; r++) {
            DSU dsu = createDSU(variant, n);
            ExperimentMetricsAggregator mc = new ExperimentMetricsAggregator();
            MSTKruskal kruskal = new MSTKruskal(dsu);

            kruskal.init(n); // makeSet fora da janela
            dsu.enableMetrics(mc);
            mc.startTimer();
            MSTResult mst = kruskal.compute(n, edges);
            mc.stopTimer();

            collectors[r] = mc;

            if (r == 0) {
                validateMST(variant, n, mst);
            }
        }

        return new BenchmarkResult(variant, n, collectors);
    }

    /**
     * Kruskal seguido de {@code queryMultiplier * n} findSet aleatórios na
     * MESMA instância DSU, sem recriá-la.
     *
     * A janela de medição cobre Kruskal + queries para contabilizar
     * corretamente a amortização da path compression do FullTarjan.
     *
     * @param variant nome da variante (UnionRank ou FullTarjan)
     */
    public BenchmarkResult runStress(String variant, int n, Edge[] edges) {
        ExperimentMetricsAggregator[] collectors = new ExperimentMetricsAggregator[repetitions];

        int numQueries = queryMultiplier * n;
        int[] queryTargets = preGenerateQueryTargets(n, numQueries);

        for (int r = 0; r < repetitions; r++) {
            DSU dsu = createDSU(variant, n);
            ExperimentMetricsAggregator mc = new ExperimentMetricsAggregator();
            MSTKruskal kruskal = new MSTKruskal(dsu);

            kruskal.init(n); // makeSet fora da janela
            dsu.enableMetrics(mc);
            mc.startTimer();

            kruskal.compute(n, edges);

            for (int q = 0; q < numQueries; q++) {
                dsu.findSet(queryTargets[q]);
            }

            mc.stopTimer();
            collectors[r] = mc;
        }

        return new BenchmarkResult(variant, n, collectors);
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

    // Pré-gera os índices das queries para não contaminar o timer com nextInt.
    private int[] preGenerateQueryTargets(int n, int numQueries) {
        Random rng = new Random(seed + 1);
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
}