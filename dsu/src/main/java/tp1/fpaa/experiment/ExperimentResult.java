package tp1.fpaa.experiment;

import tp1.fpaa.metrics.MetricsCollector;
import tp1.fpaa.metrics.StatisticsCalculator;

/**
 * Resultado consolidado de um experimento (todas as repetições de uma
 * variante/n).
 *
 * Value object imutável: carrega os coletores brutos e expõe as estatísticas
 * calculadas. Não sabe nada sobre formatação ou saída.
 */
public class ExperimentResult {

    private final String variant;
    private final int n;
    private final MetricsCollector[] collectors;

    public ExperimentResult(String variant, int n, MetricsCollector[] collectors) {
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

    /** Tempo médio em milissegundos. */
    public double avgTimeMs() {
        return StatisticsCalculator.averageTimeNano(collectors) / 1_000_000.0;
    }

    /** Desvio padrão do tempo em milissegundos. */
    public double stdTimeMs() {
        return StatisticsCalculator.stdDevTimeNano(collectors) / 1_000_000.0;
    }

    /** Média de acessos ao parent[]. */
    public double avgAccesses() {
        return StatisticsCalculator.averageAccesses(collectors);
    }

    /** Desvio padrão de acessos ao parent[]. */
    public double stdAccesses() {
        return StatisticsCalculator.stdDevAccesses(collectors);
    }

    /** Acesso aos coletores brutos, útil para testes. */
    public MetricsCollector[] getCollectors() {
        return collectors;
    }
}