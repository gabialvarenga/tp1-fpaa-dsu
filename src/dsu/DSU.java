package dsu;

/**
 * Interface abstrata para Disjoint Set Union.
 */
public interface DSU {

    /**
     * Inicializa a estrutura com n elementos.
     */
    void reset(int n);

    /**
     * Retorna o representante do conjunto.
     */
    int find(int x);

    /**
     * Une dois conjuntos.
     */
    void union_sets(int a, int b);

    /**
     * Contador de acessos ao vetor parent.
     */
    long get_acessos_parent();

    /**
     * Contador de acessos ao vetor rank.
     */
    long get_acessos_rank();
}
