package tp1.fpaa.dsu;

import tp1.fpaa.metrics.MetricsCollector;

/**
 * Implementação de DSU (Disjoint Set Union / Union-Find) com Union by Rank e
 * Path Compression.
 *
 * Representa uma coleção de conjuntos disjuntos usando uma floresta de árvores.
 * Combina union by rank (árvores balanceadas) e path compression (comprime
 * caminhos durante findSet)
 * resultando em complexidade amortizada O(α(n)).
 */
public class DSUFullTarjan implements DSU {

    /**
     * parent[x] aponta para o pai de x. Se parent[x] == x, x é raiz
     * (representante).
     * Durante findSet, é atualizado para apontar diretamente à raiz (path
     * compression).
     */
    private final int[] parent;

    /** rank[x] é uma cota superior da altura da árvore enraizada em x. */
    private final int[] rank;

    /** Número máximo de elementos. Índices válidos: 0 até capacity - 1. */
    private final int capacity;

    /** Coletor de métricas. Nulo quando desabilitado. */
    private MetricsCollector metrics = null;

    /**
     * Cria a estrutura com capacidade para {@code n} elementos.
     * Nenhum conjunto é inicializado; use makeSet(x) antes de operar.
     *
     * @param n capacidade total (1 <= n <= 100.000.000)
     */
    public DSUFullTarjan(int n) {
        if (n <= 0 || n > 100_000_000) {
            throw new IllegalArgumentException(
                    "Capacidade deve estar em [1, 100000000]. Recebido: " + n);
        }
        this.capacity = n;
        this.parent = new int[n];
        this.rank = new int[n];
    }

    @Override
    public void enableMetrics(MetricsCollector m) {
        this.metrics = m;
    }

    public void disableMetrics() {
        this.metrics = null;
    }

    public int capacity() {
        return capacity;
    }

    /** Lê parent[x] e contabiliza o acesso se métricas estiverem ativas. */
    private int readParent(int x) {
        if (metrics != null)
            metrics.incParentAccess();
        return parent[x];
    }

    /** Escreve parent[x] e contabiliza o acesso se métricas estiverem ativas. */
    private void writeParent(int x, int value) {
        if (metrics != null)
            metrics.incParentAccess();
        parent[x] = value;
    }

    /** Lê rank[x] e contabiliza o acesso se métricas estiverem ativas. */
    private int readRank(int x) {
        if (metrics != null)
            metrics.incParentAccess();
        return rank[x];
    }

    /** Escreve rank[x] e contabiliza o acesso se métricas estiverem ativas. */
    private void writeRank(int x, int value) {
        if (metrics != null)
            metrics.incParentAccess();
        rank[x] = value;
    }

    /**
     * Retorna a profundidade de {@code x} na árvore.
     */
    public int depth(int x) {
        int d = 0;
        while (readParent(x) != x) {
            x = readParent(x);
            d++;
        }
        return d;
    }

    /**
     * Inicializa x como um conjunto unitário (parent[x] = x, rank[x] = 0).
     * Complexidade: O(1).
     */
    @Override
    public void makeSet(int x) {
        writeParent(x, x);
        writeRank(x, 0);
    }

    /**
     * Retorna o representante do conjunto contendo x, subindo a árvore até a raiz.
     * Aplica path compression: cada nó visitado é reapontado diretamente para a
     * raiz.
     * Complexidade amortizada: O(α(n)).
     */
    @Override
    public int findSet(int x) {
        int p = readParent(x);
        if (p != x) {
            int rep = findSet(p);
            writeParent(x, rep);
            return rep;
        }
        return x;
    }

    /**
     * Une os conjuntos de x e y via union by rank.
     * Complexidade amortizada: O(α(n)).
     */
    @Override
    public void union(int x, int y) {
        int rx = findSet(x);
        int ry = findSet(y);
        if (rx != ry) {
            link(rx, ry);
        }
    }

    /**
     * Conecta duas raízes usando union by rank.
     * A árvore de menor rank é anexada sob a de maior rank.
     * Se os ranks são iguais, ry torna-se raiz e seu rank é incrementado.
     * Complexidade: O(1).
     */
    private void link(int x, int y) {
        int rx = readRank(x);
        int ry = readRank(y);
        if (rx > ry) {
            writeParent(y, x);
        } else {
            writeParent(x, y);
            if (rx == ry) {
                writeRank(y, ry + 1);
            }
        }
    }

    /**
     * Retorna true se x e y pertencem ao mesmo conjunto.
     * Complexidade amortizada: O(α(n)).
     */
    public boolean connected(int x, int y) {
        return findSet(x) == findSet(y);
    }

    /**
     * Representação textual dos arrays parent e rank.
     * Para capacidades acima de 20, exibe apenas o tamanho.
     */
    @Override
    public String toString() {
        if (capacity > 20) {
            return "DSUFullTarjan{capacity=" + capacity + "}";
        }
        StringBuilder sb = new StringBuilder("DSUFullTarjan{capacity=")
                .append(capacity).append(", parent=[");
        for (int i = 0; i < capacity; i++) {
            sb.append(parent[i]);
            if (i < capacity - 1)
                sb.append(", ");
        }
        sb.append("], rank=[");
        for (int i = 0; i < capacity; i++) {
            sb.append(rank[i]);
            if (i < capacity - 1)
                sb.append(", ");
        }
        return sb.append("]}").toString();
    }
}