package tp1.fpaa.benchmark;

import java.util.Random;

import tp1.fpaa.algorithm.mst.Edge;

/**
 * Fábrica de grafos aleatórios para os experimentos comparativos.
 *
 * Centraliza a geração de grafos conexos com parâmetros configuráveis,
 * isolando essa responsabilidade da orquestração dos experimentos.
 */
public class RandomConnectedGraphFactory {

    private final long seed;

    /**
     * @param seed semente para reprodutibilidade
     */
    public RandomConnectedGraphFactory(long seed) {
        this.seed = seed;
    }

    /**
     * Garante conectividade via cadeia inicial (0→1→2→…→n-1)
     * e preenche o restante com arestas aleatórias distintas.
     *
     * @param m número total de arestas (deve ser >= n-1)
     */
    public Edge[] generate(int n, int m) {
        Random rng = new Random(seed);
        Edge[] edges = new Edge[m];
        int idx = 0;

        for (int i = 0; i < n - 1 && idx < m; i++) {
            edges[idx++] = new Edge(i, i + 1, rng.nextInt(10_000) + 1);
        }

        while (idx < m) {
            int u = rng.nextInt(n);
            int v = rng.nextInt(n);
            if (u != v) {
                edges[idx++] = new Edge(u, v, rng.nextInt(10_000) + 1);
            }
        }

        return edges;
    }

    /**
     * Calcula o número de arestas para um grafo esparso (~3n),
     * limitado ao máximo possível n*(n-1)/2.
     * Usa long para evitar overflow em n grande.
     */
    public int sparseEdgeCount(int n) {
        long max = (long) n * (n - 1) / 2;
        return (int) Math.min(3L * n, max);
    }
}