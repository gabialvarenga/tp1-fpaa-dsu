package tp1.fpaa.algorithm.mst;

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
}