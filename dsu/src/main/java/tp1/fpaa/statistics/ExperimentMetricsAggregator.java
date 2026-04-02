package tp1.fpaa.statistics;

public final class ExperimentMetricsAggregator {

    private static final long TIMER_NOT_STARTED = -1L;
    private long parentAccesses = 0L;
    private long startNano = TIMER_NOT_STARTED;
    private long totalNano = 0L;
    private boolean timerStopped = false;

    public void incParentAccess() {
        parentAccesses++;
    }

    public void startTimer() {
        if (startNano != TIMER_NOT_STARTED) {
            throw new IllegalStateException(
                    "startTimer() já foi chamado. Crie uma nova instância para uma nova execução.");
        }
        startNano = System.nanoTime();
    }

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