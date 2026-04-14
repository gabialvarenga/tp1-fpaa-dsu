package tp1.fpaa.algorithm.dsu;

import tp1.fpaa.statistics.ExperimentMetricsAggregator;

public class DSUNaive implements DSU {

    private final int[] parent;

    private ExperimentMetricsAggregator metrics = null;

    public DSUNaive(int n) {
        if (n < 1) {
            throw new IllegalArgumentException(
                    "Capacidade deve ser positiva. Recebido: " + n);
        }
        this.parent = new int[n];
    }

    @Override
    public void enableMetrics(ExperimentMetricsAggregator m) {
        this.metrics = m;
    }

    private int readParent(int x) {
        if (metrics != null)
            metrics.incParentAccess();
        return parent[x];
    }

    private void writeParent(int x, int value) {
        if (metrics != null)
            metrics.incParentAccess();
        parent[x] = value;
    }

    @Override
    public void makeSet(int x) {
        writeParent(x, x);
    }

    @Override
    public int findSet(int x) {
        int p = readParent(x);
        while (p != x) {
            x = p;
            p = readParent(x);
        }
        return x;
    }

    @Override
    public void union(int x, int y) {
        int rx = findSet(x);
        int ry = findSet(y);
        if (rx != ry) {
            writeParent(rx, ry);
        }
    }
}