package tp1.fpaa.experiment;

import tp1.fpaa.statistics.DescriptiveStatisticsCalculator;
import tp1.fpaa.statistics.ExperimentMetricsAggregator;

public class MSTBenchmarkResult {

    private final String variant;
    private final int n;
    private final ExperimentMetricsAggregator[] collectors;

    public MSTBenchmarkResult(String variant, int n, ExperimentMetricsAggregator[] collectors) {
        this.variant = variant;
        this.n = n;
        this.collectors = collectors;
    }

    public String getVariant() {
        return variant;
    }

    public int getN() {
        return n;
    }

    public double avgTimeMs() {
        return DescriptiveStatisticsCalculator.averageTimeNano(collectors) / 1_000_000.0;
    }

    public double stdTimeMs() {
        return DescriptiveStatisticsCalculator.stdDevTimeNano(collectors) / 1_000_000.0;
    }

    public double avgAccesses() {
        return DescriptiveStatisticsCalculator.averageAccesses(collectors);
    }

    public double stdAccesses() {
        return DescriptiveStatisticsCalculator.stdDevAccesses(collectors);
    }

    public ExperimentMetricsAggregator[] getCollectors() {
        return collectors;
    }
}