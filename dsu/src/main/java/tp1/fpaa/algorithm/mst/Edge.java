package tp1.fpaa.algorithm.mst;

/**
 * Aresta ponderada não direcionada. Ordenável por peso crescente para uso
 * direto em {@link java.util.Arrays#sort} pelo algoritmo de Kruskal.
 */
public class Edge implements Comparable<Edge> {

    public final int u;
    public final int v;
    public final int weight;

    /**
     * @param u      primeiro vértice (índice 0-based)
     * @param v      segundo vértice (índice 0-based)
     * @param weight peso da aresta
     */
    public Edge(int u, int v, int weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public String toString() {
        return "Edge{u=" + u + ", v=" + v + ", weight=" + weight + "}";
    }
}