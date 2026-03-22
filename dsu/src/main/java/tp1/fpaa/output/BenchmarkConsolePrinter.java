package tp1.fpaa.output;

import tp1.fpaa.benchmark.BenchmarkResult;

/**
 * Responsável por toda a formatação e exibição dos resultados no console.
 *
 * É a classe que chama System.out diretamente, tornando
 * trivial substituir a saída (arquivo, CSV, HTML) sem tocar na lógica dos
 * experimentos.
 */
public class BenchmarkConsolePrinter {

    private static final int LINE_WIDTH = 84;

    public void printKruskalHeader(int repetitions, long seed) {
        printHeader(
                "Experimento comparativo - Kruskal com variantes de DSU",
                "Kruskal (grafo esparso ~3n arestas)",
                repetitions, seed,
                "Naive: ate 100.000 vertices | UnionRank e FullTarjan: ate 1.000.000");
    }

    public void printStressHeader(int queryMultiplier, int repetitions, long seed) {
        System.out.println();
        printHeader(
                "Experimento comparativo - Stress de queries (reutilizacao da instancia DSU)",
                "Kruskal + " + queryMultiplier + "*n findSet repetidos (mesma instancia DSU)",
                repetitions, seed,
                "Naive omitido (custo O(n) por query tornaria o experimento inviavel)");
    }

    public void printSeparator() {
        System.out.println("-".repeat(LINE_WIDTH));
    }

    public void printRow(BenchmarkResult result) {
        System.out.printf(
                "| %-10s | %9d | %9.3f +/- %7.3f ms | %13.0f +/- %7.0f |%n",
                result.getVariant(),
                result.getN(),
                result.avgTimeMs(),
                result.stdTimeMs(),
                result.avgAccesses(),
                result.stdAccesses());
    }

    private void printHeader(String title, String subtitle,
            int repetitions, long seed, String note) {
        System.out.println("=".repeat(LINE_WIDTH));
        System.out.println("  " + title);
        System.out.println("  " + subtitle);
        System.out.printf("  Repeticoes: %d | Semente: %d%n", repetitions, seed);
        System.out.println("  " + note);
        System.out.println("=".repeat(LINE_WIDTH));
        System.out.printf("| %-10s | %9s | %-25s | %-23s |%n",
                "Variante", "n", "Tempo medio (ms)", "Acessos parent[]");
        System.out.println("-".repeat(LINE_WIDTH));
    }
}