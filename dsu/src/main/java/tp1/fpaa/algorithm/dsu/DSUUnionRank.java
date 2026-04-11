package tp1.fpaa.algorithm.dsu;

import tp1.fpaa.statistics.ExperimentMetricsAggregator;

public class DSUUnionRank implements DSU {

    private final int[] parent;

    private final int[] rank;

    private final int capacity;

    private ExperimentMetricsAggregator metrics = null;

    public DSUUnionRank(int n) {
        if (n < 1) {
            throw new IllegalArgumentException(
                    "Capacidade deve ser positiva. Recebido: " + n);
        }
        this.capacity = n;
        this.parent = new int[n];
        this.rank = new int[n];
    }

    @Override
    public void enableMetrics(ExperimentMetricsAggregator m) {
        this.metrics = m;
    }

    public void disableMetrics() {
        this.metrics = null;
    }

    public int capacity() {
        return capacity;
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
            if (p == x) break;
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
            return findSet(p);
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

    public boolean connected(int x, int y) {
        return findSet(x) == findSet(y);
    }

    @Override
    public String toString() {
        if (capacity > 20) {
            return "DSUUnionRank{capacity=" + capacity + "}";
        }
        StringBuilder sb = new StringBuilder("DSUUnionRank{capacity=")
                .append(capacity).append(", parent=[");
        for (int i = 0; i < capacity; i++) {
            sb.append(parent[i]);
            if (i < capacity - 1)
                sb.append(", ");
        }
        sb.append("], rank=[");
        for (int i = 0; i < capacity; i++) {
            sb.append(rank[i]);
            if (i < capacity - 1)
                sb.append(", ");
        }
        return sb.append("]}").toString();
    }
}
