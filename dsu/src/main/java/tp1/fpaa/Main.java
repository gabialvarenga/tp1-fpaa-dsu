package tp1.fpaa;

import tp1.fpaa.algorithm.mst.Edge;
import tp1.fpaa.benchmark.BenchmarkRunner;
import tp1.fpaa.benchmark.RandomConnectedGraphFactory;
import tp1.fpaa.output.BenchmarkConsolePrinter;

/**
 * Geração de grafos, execução e formatação são delegadas às classes especializadas.
 *
 * Fluxo:
 * RandomConnectedGraphFactory gera os grafos
 * BenchmarkRunner: executa Kruskal / stress e devolve BenchmarkResult
 * BenchmarkConsolePrinter: formata e exibe no console
 */
public class Main {

    private static final int[] SIZES = {
            500, 1_000, 5_000, 10_000, 50_000, 100_000, 500_000, 1_000_000
    };

    private static final int REPETITIONS = 5;
    private static final long SEED = 42L;
    private static final int QUERY_MULTIPLIER = 50;

    public static void main(String[] args) {

        RandomConnectedGraphFactory factory = new RandomConnectedGraphFactory(SEED);
        BenchmarkRunner runner = new BenchmarkRunner(REPETITIONS, SEED, QUERY_MULTIPLIER);
        BenchmarkConsolePrinter printer = new BenchmarkConsolePrinter();

        // Experimento 1: Kruskal puro
        printer.printKruskalHeader(REPETITIONS, SEED);

        for (int n : SIZES) {
            Edge[] edges = factory.generate(n, factory.sparseEdgeCount(n));

            if (n <= 50_000) { // Naive é O(n) por operação; inviável para n maior
                printer.printRow(runner.runKruskal("Naive", n, edges));
            }
            printer.printRow(runner.runKruskal("UnionRank", n, edges));
            printer.printRow(runner.runKruskal("FullTarjan", n, edges));
            printer.printSeparator();
        }

        // Experimento 2: Stress de queries
        printer.printStressHeader(QUERY_MULTIPLIER, REPETITIONS, SEED);

        for (int n : SIZES) {
            Edge[] edges = factory.generate(n, factory.sparseEdgeCount(n));

            printer.printRow(runner.runStress("UnionRank", n, edges));
            printer.printRow(runner.runStress("FullTarjan", n, edges));
            printer.printSeparator();
        }
    }
}