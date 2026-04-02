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
        return Math.sqrt(variance / collectors.length);
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
        return Math.sqrt(variance / collectors.length);
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