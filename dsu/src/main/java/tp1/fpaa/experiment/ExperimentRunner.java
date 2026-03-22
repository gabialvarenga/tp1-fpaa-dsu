package tp1.fpaa.experiment;

import tp1.fpaa.dsu.DSU;
import tp1.fpaa.dsu.DSUNaive;
import tp1.fpaa.dsu.DSUUnionRank;
import tp1.fpaa.dsu.DSUFullTarjan;
import tp1.fpaa.graph.Edge;
import tp1.fpaa.graph.KruskalMST;
import tp1.fpaa.graph.MSTResult;
import tp1.fpaa.metrics.MetricsCollector;

import java.util.Random;

/**
 * Orquestra a execução dos experimentos comparativos entre variantes de DSU.
 *
 * Não conhece formatação, saída ou geração de grafos — recebe o grafo pronto
 * e devolve um {@link ExperimentResult} com os dados brutos. Isso permite
 * testar a lógica do experimento de forma isolada e trocar a camada de
 * exibição sem alterar esta classe.
 */
public class ExperimentRunner {

    private final int repetitions;
    private final long seed;
    private final int queryMultiplier;

    /**
     * @param repetitions     número de repetições por configuração
     * @param seed            semente usada para gerar os índices de query no stress
     * @param queryMultiplier fator multiplicador de n para o número de queries no
     *                        stress
     */
    public ExperimentRunner(int repetitions, long seed, int queryMultiplier) {
        this.repetitions = repetitions;
        this.seed = seed;
        this.queryMultiplier = queryMultiplier;
    }

    /**
     * Executa o experimento de Kruskal puro para uma variante.
     *
     * O DSU é recriado a cada repetição. makeSet é executado fora da janela
     * de medição; apenas findSet e union são contabilizados.
     *
     * @param variant nome da variante (Naive, UnionRank ou FullTarjan)
     * @param n       número de vértices
     * @param edges   arestas do grafo
     * @return resultado consolidado com métricas de todas as repetições
     */
    public ExperimentResult runKruskal(String variant, int n, Edge[] edges) {
        MetricsCollector[] collectors = new MetricsCollector[repetitions];

        for (int r = 0; r < repetitions; r++) {
            DSU dsu = createDSU(variant, n);
            MetricsCollector mc = new MetricsCollector();
            KruskalMST kruskal = new KruskalMST(dsu);

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

        return new ExperimentResult(variant, n, collectors);
    }

    /**
     * Executa o experimento de stress: Kruskal seguido de queryMultiplier*n
     * findSet aleatórios na MESMA instância DSU, sem recriá-la.
     *
     * A janela de medição cobre Kruskal + queries para contabilizar
     * corretamente a amortização da path compression do FullTarjan.
     *
     * @param variant nome da variante (UnionRank ou FullTarjan)
     * @param n       número de vértices
     * @param edges   arestas do grafo
     * @return resultado consolidado com métricas de todas as repetições
     */
    public ExperimentResult runStress(String variant, int n, Edge[] edges) {
        MetricsCollector[] collectors = new MetricsCollector[repetitions];

        int numQueries = queryMultiplier * n;
        int[] queryTargets = preGenerateQueryTargets(n, numQueries);

        for (int r = 0; r < repetitions; r++) {
            DSU dsu = createDSU(variant, n);
            MetricsCollector mc = new MetricsCollector();
            KruskalMST kruskal = new KruskalMST(dsu);

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

        return new ExperimentResult(variant, n, collectors);
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

    /**
     * Pré-gera os índices das queries para não contaminar o timer com nextInt.
     */
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