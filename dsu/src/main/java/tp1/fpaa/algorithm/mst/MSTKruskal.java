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

    public void init(int numVertices) {
        for (int i = 0; i < numVertices; i++) {
            dsu.makeSet(i);
        }
    }

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
                dsu.union(ru, rv);
            }
        }

        Edge[] result = new Edge[mstSize];
        System.arraycopy(mstEdges, 0, result, 0, mstSize);

        return new MSTResult(result, totalCost);
    }
}
