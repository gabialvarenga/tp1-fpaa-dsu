package tp1.fpaa.experiment;

import java.util.Random;

import tp1.fpaa.algorithm.mst.Edge;

public class RandomConnectedGraphFactory {

    private final long seed;

    public RandomConnectedGraphFactory(long seed) {
        this.seed = seed;
    }

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

    public int sparseEdgeCount(int n) {
        long max = (long) n * (n - 1) / 2;
        return (int) Math.min(3L * n, max);
    }
}