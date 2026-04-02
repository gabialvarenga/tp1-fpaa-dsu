package tp1.fpaa.algorithm.mst;

import java.util.Arrays;

import tp1.fpaa.algorithm.dsu.DSU;

/**
 * Algoritmo de Kruskal para Árvore Geradora Mínima.
 *
 * A implementação de {@link DSU} é injetada pelo chamador para permitir
 * comparação de desempenho entre as variantes (Naive, UnionRank, FullTarjan).
 *
 * Complexidade: O(|E| log |E|) dominado pela ordenação; as operações de DSU
 * contribuem O(|E| · α(|V|)) com DSUFullTarjan.
 *
 * Fundamentação: PATROCÍNIO, Zenilton. Árvore Geradora Mínima. PUC Minas —
 * Fundamentos de Grafos e Computabilidade.
 */
public class MSTKruskal {

    private final DSU dsu;

    public MSTKruskal(DSU dsu) {
        this.dsu = dsu;
    }

    /**
     * Deve ser chamado ANTES de enableMetrics() para que os acessos de
     * inicialização não contaminem as métricas de findSet e union.
     *
     * @param numVertices número de vértices; índices 0 até n − 1
     */
    public void init(int numVertices) {
        for (int i = 0; i < numVertices; i++) {
            dsu.makeSet(i);
        }
    }

    /**
     * A janela de medição deve envolver apenas esta chamada para isolar findSet e 
     * union nas comparações.
     * @param numVertices número de vértices; índices 0 até n − 1
     * @param edges       arestas do grafo
     * @return {@link MSTResult} com as arestas selecionadas e o custo total
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

        return computeOnSortedEdges(numVertices, sorted);
    }


    public MSTResult computeOnSortedEdges(int numVertices, Edge[] sortedEdges) {
        if (numVertices < 1) {
            throw new IllegalArgumentException(
                    "numVertices deve ser positivo. Recebido: " + numVertices);
        }
        if (sortedEdges == null) {
            throw new IllegalArgumentException("sortedEdges nao pode ser nulo.");
        }

        Edge[] mstEdges = new Edge[numVertices - 1];
        int mstSize = 0;
        long totalCost = 0;

        for (int j = 0; j < sortedEdges.length && mstSize < numVertices - 1; j++) {
            Edge e = sortedEdges[j];

            int ru = dsu.findSet(e.u);
            int rv = dsu.findSet(e.v);

            if (ru != rv) {
                mstEdges[mstSize++] = e;
                totalCost += e.weight;
                dsu.union(ru, rv);
            }
        }

        Edge[] result = new Edge[mstSize];
        System.arraycopy(mstEdges, 0, result, 0, mstSize);

        return new MSTResult(result, totalCost);
    }
}
