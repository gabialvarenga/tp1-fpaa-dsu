package tp1.fpaa.dsu;

import tp1.fpaa.metrics.MetricsCollector;

/**
 * Implementação naive de DSU (Disjoint Set Union / Union-Find).
 *
 * Representa uma coleção de conjuntos disjuntos usando uma floresta de árvores,
 * onde cada nó aponta para seu pai e a raiz é o representante do conjunto.
 * Não utiliza otimizações (sem path compression ou union by rank).
 * Complexidade geral: O(n) no pior caso para findSet e union.
 */
public class DSUNaive implements DSU {

    /**
     * parent[x] aponta para o pai de x. Se parent[x] == x, x é raiz
     * (representante).
     */
    private final int[] parent;

    /** Número máximo de elementos. Índices válidos: 0 até capacity - 1. */
    private final int capacity;

    /** Coletor de métricas. Nulo quando desabilitado. */
    private MetricsCollector metrics = null;

    /**
     * Cria a estrutura com capacidade para {@code n} elementos.
     * Nenhum conjunto é inicializado; use makeSet(x) antes de operar.
     *
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
     * Inicializa x como um conjunto unitário (parent[x] = x).
     * Complexidade: O(1).
     */
    @Override
    public void makeSet(int x) {
        writeParent(x, x);
    }

    /**
     * Retorna o representante do conjunto contendo x,
     * subindo a árvore até encontrar a raiz. Sem path compression.
     *
     * Implementação iterativa para evitar StackOverflowError em cadeias
     * longas (pior caso com n grande produz recursão de profundidade n-1,
     * excedendo o limite de pilha da JVM).
     *
     * Complexidade: O(n) no pior caso.
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

    /**
     * Une os conjuntos de x e y ligando uma raiz à outra.
     * Sem heurística de balanceamento; pode gerar árvores degeneradas.
     * Complexidade: O(n) no pior caso.
     */
    @Override
    public void union(int x, int y) {
        int rx = findSet(x);
        int ry = findSet(y);
        if (rx != ry) {
            writeParent(rx, ry);
        }
    }

    /**
     * Retorna true se x e y pertencem ao mesmo conjunto.
     * Complexidade: O(n) no pior caso.
     */
    public boolean connected(int x, int y) {
        return findSet(x) == findSet(y);
    }

    /**
     * Representação textual do array parent.
     * Para capacidades acima de 20, exibe apenas o tamanho.
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