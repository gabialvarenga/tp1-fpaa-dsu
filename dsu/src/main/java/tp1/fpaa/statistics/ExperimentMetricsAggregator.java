package tp1.fpaa.statistics;

/**
 * Coleta dados brutos de uma única execução de DSU.
 *
 * Registra acessos ao array {@code parent[]} e mede o tempo de um bloco de
 * operações. Não calcula estatísticas; para consolidar múltiplas execuções,
 * use {@link DescriptiveStatisticsCalculator}.
 */
public final class ExperimentMetricsAggregator {

    // -1 como sentinela: distingue "nunca iniciado" de um instante de tempo válido (>= 0)
    private static final long TIMER_NOT_STARTED = -1L;

    private long parentAccesses = 0L;
    private long startNano = TIMER_NOT_STARTED;
    private long totalNano = 0L;
    private boolean timerStopped = false;

    public void incParentAccess() {
        parentAccesses++;
    }

    /**
     * Marca o início da janela de medição.
     * Deve ser chamado com o DSU já inicializado (makeSet fora da janela).
     *
     * @throws IllegalStateException se o timer já tiver sido iniciado
     */
    public void startTimer() {
        if (startNano != TIMER_NOT_STARTED) {
            throw new IllegalStateException(
                    "startTimer() já foi chamado. Crie uma nova instância para uma nova execução.");
        }
        startNano = System.nanoTime();
    }

    /**
     * Fecha a janela de medição e armazena o tempo total.
     *
     * @throws IllegalStateException se startTimer() não tiver sido chamado antes
     * @throws IllegalStateException se stopTimer() já tiver sido chamado
     */
    public void stopTimer() {
        if (startNano == TIMER_NOT_STARTED) {
            throw new IllegalStateException(
                    "stopTimer() chamado antes de startTimer().");
        }
        if (timerStopped) {
            throw new IllegalStateException(
                    "stopTimer() já foi chamado nesta execução.");
        }
        totalNano = System.nanoTime() - startNano;
        timerStopped = true;
    }

    public long getParentAccesses() {
        return parentAccesses;
    }

    /**
     * @throws IllegalStateException se stopTimer() ainda não foi chamado
     */
    public long getTotalNano() {
        if (!timerStopped) {
            throw new IllegalStateException(
                    "getTotalNano() chamado antes de stopTimer().");
        }
        return totalNano;
    }

    public boolean isFinished() {
        return timerStopped;
    }
}