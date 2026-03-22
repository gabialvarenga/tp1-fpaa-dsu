package tp1.fpaa.metrics;

/**
 * Consolida múltiplas execuções de {@link MetricsCollector} calculando
 * média e desvio padrão de tempo e acessos ao {@code parent[]}.
 *
 * Classe utilitária sem estado; todos os métodos são estáticos.
 */
public final class StatisticsCalculator {

    private StatisticsCalculator() {
    }

    /**
     * Retorna a média do tempo total (em nanosegundos) entre as execuções.
     *
     * @param collectors uma entrada por repetição; todas devem estar finalizadas
     * @return média em nanosegundos
     * @throws IllegalArgumentException se collectors for nulo, vazio ou contiver
     *                                  entradas não finalizadas
     */
    public static double averageTimeNano(MetricsCollector[] collectors) {
        validate(collectors);
        double sum = 0;
        for (MetricsCollector c : collectors)
            sum += c.getTotalNano();
        return sum / collectors.length;
    }

    /**
     * Retorna o desvio padrão do tempo total (em nanosegundos) entre as execuções.
     *
     * @param collectors uma entrada por repetição; todas devem estar finalizadas
     * @return desvio padrão em nanosegundos
     * @throws IllegalArgumentException se collectors for nulo, vazio ou contiver
     *                                  entradas não finalizadas
     */
    public static double stdDevTimeNano(MetricsCollector[] collectors) {
        validate(collectors);
        double avg = averageTimeNano(collectors);
        double variance = 0;
        for (MetricsCollector c : collectors)
            variance += Math.pow(c.getTotalNano() - avg, 2);
        return Math.sqrt(variance / collectors.length);
    }

    /**
     * Retorna a média de acessos ao {@code parent[]} entre as execuções.
     *
     * @param collectors uma entrada por repetição; todas devem estar finalizadas
     * @return média de acessos
     * @throws IllegalArgumentException se collectors for nulo, vazio ou contiver
     *                                  entradas não finalizadas
     */
    public static double averageAccesses(MetricsCollector[] collectors) {
        validate(collectors);
        double sum = 0;
        for (MetricsCollector c : collectors)
            sum += c.getParentAccesses();
        return sum / collectors.length;
    }

    /**
     * Retorna o desvio padrão de acessos ao {@code parent[]} entre as execuções.
     *
     * @param collectors uma entrada por repetição; todas devem estar finalizadas
     * @return desvio padrão de acessos
     * @throws IllegalArgumentException se collectors for nulo, vazio ou contiver
     *                                  entradas não finalizadas
     */
    public static double stdDevAccesses(MetricsCollector[] collectors) {
        validate(collectors);
        double avg = averageAccesses(collectors);
        double variance = 0;
        for (MetricsCollector c : collectors)
            variance += Math.pow(c.getParentAccesses() - avg, 2);
        return Math.sqrt(variance / collectors.length);
    }

    /**
     * Valida o array de coletores antes do cálculo.
     *
     * @throws IllegalArgumentException se collectors for nulo, vazio ou contiver
     *                                  entradas nulas ou não finalizadas
     */
    private static void validate(MetricsCollector[] collectors) {
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