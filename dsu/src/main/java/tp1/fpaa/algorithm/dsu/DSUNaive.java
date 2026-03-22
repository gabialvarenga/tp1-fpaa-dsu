package tp1.fpaa.algorithm.dsu;

import tp1.fpaa.statistics.ExperimentMetricsAggregator;

/**
 * DSU sem otimizações sem path compression ou union by rank.
 * Serve como baseline para comparação de desempenho com implementações
 * otimizadas. Não use em produção: O(n) por operação no pior caso.
 */
public class DSUNaive implements DSU {

    // parent[x] == x indica que x é raiz (representante do conjunto)
    private final int[] parent;

    private final int capacity;

    // null quando métricas estão desabilitadas
    private ExperimentMetricsAggregator metrics = null;

    /**
     * @param n capacidade total (n >= 1)
     */
    public DSUNaive(int n) {
        if (n < 1) {
            throw new IllegalArgumentException(
                    "Capacidade deve ser positiva. Recebido: " + n);
        }
        this.capacity = n;
        this.parent = new int[n];
    }

    @Override
    public void enableMetrics(ExperimentMetricsAggregator m) {
        this.metrics = m;
    }

    public void disableMetrics() {
        this.metrics = null;
    }

    public int capacity() {
        return capacity;
    }

    private int readParent(int x) {
        if (metrics != null)
            metrics.incParentAccess();
        return parent[x];
    }

    private void writeParent(int x, int value) {
        if (metrics != null)
            metrics.incParentAccess();
        parent[x] = value;
    }

    public int depth(int x) {
        int d = 0;
        while (readParent(x) != x) {
            x = readParent(x);
            d++;
        }
        return d;
    }

    @Override
    public void makeSet(int x) {
        writeParent(x, x);
    }

    /**
     * Implementação iterativa para evitar StackOverflowError — cadeias degeneradas
     * podem atingir profundidade n-1, excedendo o limite de pilha da JVM.
     */
    @Override
    public int findSet(int x) {
        int p = readParent(x);
        while (p != x) {
            x = p;
            p = readParent(x);
        }
        return x;
    }

    @Override
    public void union(int x, int y) {
        int rx = findSet(x);
        int ry = findSet(y);
        if (rx != ry) {
            writeParent(rx, ry);
        }
    }

    public boolean connected(int x, int y) {
        return findSet(x) == findSet(y);
    }

    /**
     * Acima de 20 elementos o array não agrega valor de depuração, só polui.
     */
    @Override
    public String toString() {
        if (capacity > 20) {
            return "DSUNaive{capacity=" + capacity + "}";
        }

        StringBuilder sb = new StringBuilder("DSUNaive{capacity=")
                .append(capacity).append(", parent=[");

        for (int i = 0; i < capacity; i++) {
            sb.append(parent[i]);
            if (i < capacity - 1)
                sb.append(", ");
        }

        return sb.append("]}").toString();
    }
}