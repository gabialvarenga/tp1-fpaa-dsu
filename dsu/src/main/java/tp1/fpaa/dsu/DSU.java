package tp1.fpaa.dsu;

import tp1.fpaa.metrics.MetricsCollector;

/**
 * Estrutura de conjuntos disjuntos (Union-Find).
 *
 * Mantém uma coleção de conjuntos não sobrepostos, permitindo verificar
 * se dois elementos pertencem ao mesmo conjunto e fundir conjuntos.
 * Cada conjunto é identificado por um representante.
 */
public interface DSU {

    /**
     * Cria um conjunto unitário contendo apenas {@code x}.
     * Deve ser chamado exatamente uma vez por elemento, antes de qualquer outra
     * operação.
     *
     * @param x índice do elemento (0 ≤ x < capacidade)
     */
    public void makeSet(int x);

    /**
     * Retorna o representante do conjunto que contém {@code x}.
     * Dois elementos pertencem ao mesmo conjunto se e somente se seus
     * representantes são iguais.
     *
     * @param x índice do elemento
     * @return índice do representante do conjunto
     */
    public int findSet(int x);

    /**
     * Funde os conjuntos que contêm {@code x} e {@code y}.
     * Se já estiverem no mesmo conjunto, não faz nada.
     *
     * @param x índice do primeiro elemento
     * @param y índice do segundo elemento
     */
    public void union(int x, int y);

    /**
     * Vincula um coletor de métricas a esta instância.
     * Deve ser chamado após a inicialização e antes do início da medição.
     */
    public void enableMetrics(MetricsCollector collector);
}