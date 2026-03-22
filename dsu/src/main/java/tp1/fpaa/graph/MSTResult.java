package tp1.fpaa.graph;

/**
 * Resultado da execução do algoritmo de Kruskal.
 *
 * Encapsula as arestas que compõem a Árvore Geradora Mínima (AGM) e o
 * custo total C(T) = Σ c_e para e ∈ E(T).
 */
public class MSTResult {

    /** Arestas que compõem a AGM, na ordem em que foram selecionadas. */
    private final Edge[] edges;

    /** Custo total da AGM: soma dos pesos de todas as arestas selecionadas. */
    private final long totalCost;

    /**
     * Cria o resultado com as arestas da AGM e o custo total já calculado.
     *
     * @param edges     arestas selecionadas (exatamente n − 1 para grafo conexo)
     * @param totalCost soma dos pesos das arestas
     */
    public MSTResult(Edge[] edges, long totalCost) {
        this.edges = edges;
        this.totalCost = totalCost;
    }

    /**
     * Retorna as arestas que compõem a AGM.
     */
    public Edge[] getEdges() {
        return edges;
    }

    /**
     * Retorna o custo total da AGM.
     */
    public long getTotalCost() {
        return totalCost;
    }

    /**
     * Retorna o número de arestas na AGM.
     */
    public int edgeCount() {
        return edges.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MSTResult{totalCost=")
                .append(totalCost)
                .append(", edges=[");
        for (int i = 0; i < edges.length; i++) {
            sb.append(edges[i]);
            if (i < edges.length - 1)
                sb.append(", ");
        }
        return sb.append("]}").toString();
    }
}