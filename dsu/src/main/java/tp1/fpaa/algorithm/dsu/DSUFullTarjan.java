package tp1.fpaa.algorithm.dsu;

import tp1.fpaa.statistics.ExperimentMetricsAggregator;

public class DSUFullTarjan implements DSU {

    private final int[] parent;

    private final int[] rank;

    private ExperimentMetricsAggregator metrics = null;

    public DSUFullTarjan(int n) {
        if (n < 1) {
            throw new IllegalArgumentException(
                    "Capacidade deve ser positiva. Recebido: " + n);
        }
        this.parent = new int[n];
        this.rank = new int[n];
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

    private int readRank(int x) {
        if (metrics != null)
            metrics.incParentAccess();
        return rank[x];
    }

    private void writeRank(int x, int value) {
        if (metrics != null)
            metrics.incParentAccess();
        rank[x] = value;
    }

    public int depth(int x) {
        int d = 0;
        while (true) {
            int p = readParent(x);
            if (p == x)
                break;
            x = p;
            d++;
        }
        return d;
    }

    @Override
    public void makeSet(int x) {
        writeParent(x, x);
        writeRank(x, 0);
    }

    @Override
    public int findSet(int x) {
        int p = readParent(x);
        if (p != x) {
            int rep = findSet(p);
            if (rep != p) {
                writeParent(x, rep);
            }
            return rep;
        }
        return x;
    }

    @Override
    public void union(int x, int y) {
        int rx = findSet(x);
        int ry = findSet(y);
        if (rx != ry) {
            link(rx, ry);
        }
    }

    private void link(int x, int y) {
        int rx = readRank(x);
        int ry = readRank(y);
        if (rx > ry) {
            writeParent(y, x);
        } else {
            writeParent(x, y);
            if (rx == ry) {
                writeRank(y, ry + 1);
            }
        }
    }
}