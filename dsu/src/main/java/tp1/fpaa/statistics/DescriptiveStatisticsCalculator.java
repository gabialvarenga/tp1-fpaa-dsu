package tp1.fpaa.statistics;


/**
 * Consolida múltiplas execuções de {@link ExperimentMetricsAggregator} calculando
 * média e desvio padrão de tempo e acessos ao {@code parent[]}.
 *
 * Classe utilitária sem estado; todos os métodos são estáticos.
 */
public final class DescriptiveStatisticsCalculator {

    private DescriptiveStatisticsCalculator() {
    }

    /**
     * @param collectors uma entrada por repetição; todas devem estar finalizadas
     * @throws IllegalArgumentException se collectors for nulo, vazio ou contiver
     *                                  entradas não finalizadas
     */
    public static double averageTimeNano(ExperimentMetricsAggregator[] collectors) {
        validate(collectors);
        double sum = 0;
        for (ExperimentMetricsAggregator c : collectors)
            sum += c.getTotalNano();
        return sum / collectors.length;
    }

    /**
     * @param collectors uma entrada por repetição; todas devem estar finalizadas
     * @throws IllegalArgumentException se collectors for nulo, vazio ou contiver
     *                                  entradas não finalizadas
     */
    public static double stdDevTimeNano(ExperimentMetricsAggregator[] collectors) {
        validate(collectors);
        double avg = averageTimeNano(collectors);
        double variance = 0;
        for (ExperimentMetricsAggregator c : collectors)
            variance += Math.pow(c.getTotalNano() - avg, 2);
        return Math.sqrt(variance / collectors.length);
    }

    /**
     * @param collectors uma entrada por repetição; todas devem estar finalizadas
     * @throws IllegalArgumentException se collectors for nulo, vazio ou contiver
     *                                  entradas não finalizadas
     */
    public static double averageAccesses(ExperimentMetricsAggregator[] collectors) {
        validate(collectors);
        double sum = 0;
        for (ExperimentMetricsAggregator c : collectors)
            sum += c.getParentAccesses();
        return sum / collectors.length;
    }

    /**
     * @param collectors uma entrada por repetição; todas devem estar finalizadas
     * @throws IllegalArgumentException se collectors for nulo, vazio ou contiver
     *                                  entradas não finalizadas
     */
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