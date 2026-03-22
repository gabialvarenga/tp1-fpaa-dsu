package tp1.fpaa.graph;

import java.util.Arrays;
import tp1.fpaa.dsu.DSU;

/**
 * Implementação do Algoritmo de Kruskal para Árvore Geradora Mínima (AGM).
 *
 * Constrói a AGM incluindo gulosamente arestas em ordem não decrescente de
 * peso, rejeitando as que formariam ciclo com as já selecionadas. A detecção
 * de ciclo é delegada a uma instância de {@link DSU} injetada pelo chamador,
 * o que permite comparar o impacto de cada variante (Naive, UnionRank e
 * FullTarjan) na performance total do algoritmo.
 *
 * Complexidade: O(|E| log |E|) dominado pela ordenação das arestas.
 * As operações de DSU contribuem O(|E| · α(|V|)) com DSUFullTarjan.
 *
 * Fundamentação: PATROCÍNIO, Zenilton. Árvore Geradora Mínima. PUC Minas —
 * Fundamentos de Grafos e Computabilidade.
 */
public class KruskalMST {

    /** Estrutura DSU usada para detecção de ciclos. */
    private final DSU dsu;

    /**
     * Cria uma instância do algoritmo de Kruskal com a implementação de DSU
     * fornecida.
     *
     * @param dsu implementação de DSU a ser usada (Naive, UnionRank ou FullTarjan)
     */
    public KruskalMST(DSU dsu) {
        this.dsu = dsu;
    }

    /**
     * Inicializa o DSU com makeSet para cada vértice.
     * Deve ser chamado ANTES de enableMetrics() e startTimer() para que os
     * acessos de inicialização não sejam contabilizados nas métricas de
     * findSet e union.
     *
     * @param numVertices número de vértices do grafo; índices 0 até n − 1
     */
    public void init(int numVertices) {
        for (int i = 0; i < numVertices; i++) {
            dsu.makeSet(i);
        }
    }

    /**
     * Executa o algoritmo de Kruskal e retorna a AGM do grafo.
     *
     * Itera sobre as arestas ordenadas em ordem crescente, aceitando aquelas
     * cujos extremos pertencem a componentes distintos (findSet(u) != findSet(v))
     * e unindo os componentes via union. Termina ao selecionar n − 1 arestas
     * ou ao esgotar todas as arestas (grafo desconexo — retorna floresta
     * geradora mínima).
     *
     * Pressupõe que init(numVertices) já foi chamado antes deste método.
     * A janela de medição deve envolver apenas esta chamada, isolando
     * findSet e union nas comparações de variantes.
     *
     * Complexidade: O(|E| log |E|) no pior caso.
     *
     * @param numVertices número de vértices do grafo; índices 0 até n − 1
     * @param edges       arestas do grafo
     * @return {@link MSTResult} com as arestas selecionadas e o custo total
     * @throws IllegalArgumentException se numVertices for menor que 1 ou
     *                                  edges for nulo
     */
    public MSTResult compute(int numVertices, Edge[] edges) {
        if (numVertices < 1) {
            throw new IllegalArgumentException(
                    "numVertices deve ser positivo. Recebido: " + numVertices);
        }
        if (edges == null) {
            throw new IllegalArgumentException("edges não pode ser nulo.");
        }

        Edge[] sorted = edges.clone();
        Arrays.sort(sorted);

        Edge[] mstEdges = new Edge[numVertices - 1];
        int mstSize = 0;
        long totalCost = 0;

        for (int j = 0; j < sorted.length && mstSize < numVertices - 1; j++) {
            Edge e = sorted[j];

            int ru = dsu.findSet(e.u);
            int rv = dsu.findSet(e.v);

            if (ru != rv) {
                mstEdges[mstSize++] = e;
                totalCost += e.weight;
                dsu.union(e.u, e.v);
            }
        }

        Edge[] result = new Edge[mstSize];
        System.arraycopy(mstEdges, 0, result, 0, mstSize);

        return new MSTResult(result, totalCost);
    }
}