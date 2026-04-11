package tp1.fpaa.algorithm.dsu;

import tp1.fpaa.statistics.ExperimentMetricsAggregator;

public interface DSU {

    void makeSet(int x);

    int findSet(int x);

    void union(int x, int y);

    void enableMetrics(ExperimentMetricsAggregator collector);
}