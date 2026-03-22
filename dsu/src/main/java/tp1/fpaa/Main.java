package tp1.fpaa;

import tp1.fpaa.experiment.ExperimentRunner;
import tp1.fpaa.experiment.GraphFactory;
import tp1.fpaa.graph.Edge;
import tp1.fpaa.report.ResultPrinter;

/**
 * Ponto de entrada do experimento comparativo entre variantes de DSU.
 *
 * Configuração dos parâmetros e sequência dos experimentos. 
 * Geração de grafos, execução e formatação
 * são delegadas às classes especializadas.
 *
 * Fluxo:
 * GraphFactory → gera os grafos
 * ExperimentRunner → executa Kruskal / stress e devolve ExperimentResult
 * ResultPrinter → formata e exibe no console
 */
public class Main {

    private static final int[] SIZES = {
            500, 1_000, 5_000, 10_000, 50_000, 100_000, 500_000, 1_000_000
    };

    private static final int REPETITIONS = 5;
    private static final long SEED = 42L;
    private static final int QUERY_MULTIPLIER = 50;

    public static void main(String[] args) {

        GraphFactory factory = new GraphFactory(SEED);
        ExperimentRunner runner = new ExperimentRunner(REPETITIONS, SEED, QUERY_MULTIPLIER);
        ResultPrinter printer = new ResultPrinter();

        // --- Experimento 1: Kruskal puro ---
        printer.printKruskalHeader(REPETITIONS, SEED);

        for (int n : SIZES) {
            Edge[] edges = factory.generate(n, factory.sparseEdgeCount(n));

            if (n <= 50_000) {
                printer.printRow(runner.runKruskal("Naive", n, edges));
            }
            printer.printRow(runner.runKruskal("UnionRank", n, edges));
            printer.printRow(runner.runKruskal("FullTarjan", n, edges));
            printer.printSeparator();
        }

        // --- Experimento 2: Stress de queries ---
        printer.printStressHeader(QUERY_MULTIPLIER, REPETITIONS, SEED);

        for (int n : SIZES) {
            Edge[] edges = factory.generate(n, factory.sparseEdgeCount(n));

            printer.printRow(runner.runStress("UnionRank", n, edges));
            printer.printRow(runner.runStress("FullTarjan", n, edges));
            printer.printSeparator();
        }
    }
}