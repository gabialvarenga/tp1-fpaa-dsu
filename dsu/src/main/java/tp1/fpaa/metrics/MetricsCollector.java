package tp1.fpaa.metrics;

/**
 * Coleta dados brutos de uma única execução de DSU.
 *
 * Registra acessos ao array {@code parent[]} e mede o tempo de um bloco de
 * operações. Não calcula estatísticas; para consolidar múltiplas execuções,
 * use {@link StatisticsCalculator}.
 */
public final class MetricsCollector {

    /** Valor sentinela indicando que o timer ainda não foi iniciado. */
    private static final long TIMER_NOT_STARTED = -1L;

    private long parentAccesses = 0L;
    private long startNano = TIMER_NOT_STARTED;
    private long totalNano = 0L;
    private boolean timerStopped = false;

    /** Registra um acesso (leitura ou escrita) ao array {@code parent[]}. */
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

    /** Total de acessos ao {@code parent[]} acumulados nesta execução. */
    public long getParentAccesses() {
        return parentAccesses;
    }

    /**
     * Retorna o tempo total da execução em nanosegundos.
     *
     * @throws IllegalStateException se stopTimer() ainda não foi chamado
     */
    public long getTotalNano() {
        if (!timerStopped) {
            throw new IllegalStateException(
                    "getTotalNano() chamado antes de stopTimer().");
        }
        return totalNano;
    }

    /** Retorna {@code true} se a medição de tempo foi concluída. */
    public boolean isFinished() {
        return timerStopped;
    }
}