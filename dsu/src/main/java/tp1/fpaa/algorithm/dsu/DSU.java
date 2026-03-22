package tp1.fpaa.algorithm.dsu;

import tp1.fpaa.statistics.ExperimentMetricsAggregator;

/**
 * Contrato da estrutura Union-Find com compressão de caminho e union por rank.
 *
 * Representante de um conjunto pode mudar após chamadas a {@link #union},
 * portanto não armazene o resultado de {@link #findSet} entre operações.
 */
public interface DSU {

    /**
     * Deve ser chamado exatamente uma vez por elemento, antes de qualquer
     * {@link #findSet} ou {@link #union}. Chamadas duplicadas têm comportamento
     * indefinido.
     *
     * @param x índice do elemento (0 ≤ x < capacidade)
     */
    void makeSet(int x);

    /**
     * @param x índice do elemento
     * @return representante canônico do conjunto de {@code x}
     */
    int findSet(int x);

    /**
     * @param x índice do primeiro elemento
     * @param y índice do segundo elemento
     */
    void union(int x, int y);

    /**
     * Deve ser chamado após a inicialização e antes do início da medição.
     */
    void enableMetrics(ExperimentMetricsAggregator collector);
}