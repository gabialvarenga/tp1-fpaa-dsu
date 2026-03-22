package tp1.fpaa.graph;

/**
 * Aresta ponderada de um grafo não direcionado.
 *
 * Armazena os dois vértices extremos ({@code u} e {@code v}) e o peso
 * ({@code weight}). Implementa {@link Comparable} para ordenação crescente
 * por peso, conforme exigido pelo algoritmo de Kruskal.
 */
public class Edge implements Comparable<Edge> {

    /** Primeiro vértice da aresta. */
    public final int u;

    /** Segundo vértice da aresta. */
    public final int v;

    /** Peso da aresta. */
    public final int weight;

    /**
     * Cria uma aresta entre {@code u} e {@code v} com o peso informado.
     *
     * @param u      primeiro vértice (índice 0-based)
     * @param v      segundo vértice (índice 0-based)
     * @param weight peso da aresta
     */
    public Edge(int u, int v, int weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    /**
     * Compara esta aresta com outra pelo peso.
     * Permite ordenação crescente via {@link java.util.Arrays#sort}.
     */
    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public String toString() {
        return "Edge{u=" + u + ", v=" + v + ", weight=" + weight + "}";
    }
}