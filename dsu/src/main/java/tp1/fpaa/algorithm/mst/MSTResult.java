package tp1.fpaa.algorithm.mst;

/**
 * Resultado do algoritmo de Kruskal: arestas da AGM e custo total
 * C(T) = Σ c_e para e ∈ E(T).
 */
public class MSTResult {

    private final Edge[] edges;
    private final long totalCost;

    public MSTResult(Edge[] edges, long totalCost) {
        this.edges = edges;
        this.totalCost = totalCost;
    }

    public Edge[] getEdges() {
        return edges;
    }

    public long getTotalCost() {
        return totalCost;
    }

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