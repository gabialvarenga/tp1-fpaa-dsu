package tp1.fpaa.statistics;

public final class DescriptiveStatisticsCalculator {

    private DescriptiveStatisticsCalculator() {
    }

    public static double averageTimeNano(ExperimentMetricsAggregator[] collectors) {
        validate(collectors);
        double sum = 0;
        for (ExperimentMetricsAggregator c : collectors)
            sum += c.getTotalNano();
        return sum / collectors.length;
    }

    public static double stdDevTimeNano(ExperimentMetricsAggregator[] collectors) {
        validate(collectors);
        double avg = averageTimeNano(collectors);
        double variance = 0;
        for (ExperimentMetricsAggregator c : collectors)
            variance += Math.pow(c.getTotalNano() - avg, 2);
        return Math.sqrt(variance / (collectors.length - 1));
    }

    public static double averageAccesses(ExperimentMetricsAggregator[] collectors) {
        validate(collectors);
        double sum = 0;
        for (ExperimentMetricsAggregator c : collectors)
            sum += c.getParentAccesses();
        return sum / collectors.length;
    }

    public static double stdDevAccesses(ExperimentMetricsAggregator[] collectors) {
        validate(collectors);
        double avg = averageAccesses(collectors);
        double variance = 0;
        for (ExperimentMetricsAggregator c : collectors)
            variance += Math.pow(c.getParentAccesses() - avg, 2);
        return Math.sqrt(variance / (collectors.length - 1));
    }

    public static long medianTimeNano(ExperimentMetricsAggregator[] collectors) {
        validate(collectors);
        long[] times = new long[collectors.length];
        for (int i = 0; i < collectors.length; i++)
            times[i] = collectors[i].getTotalNano();
        java.util.Arrays.sort(times);
        int mid = times.length / 2;
        return times.length % 2 == 0 ? (times[mid - 1] + times[mid]) / 2 : times[mid];
    }

    private static void validate(ExperimentMetricsAggregator[] collectors) {
        if (collectors == null || collectors.length == 0) {
            throw new IllegalArgumentException("collectors não pode ser nulo ou vazio.");
        }
        for (int i = 0; i < collectors.length; i++) {
            if (collectors[i] == null) {
                throw new IllegalArgumentException("collectors[" + i + "] é null.");
            }
            if (!collectors[i].isFinished()) {
                throw new IllegalArgumentException(
                        "collectors[" + i + "] não foi finalizado (stopTimer() não chamado).");
            }
        }
    }
}