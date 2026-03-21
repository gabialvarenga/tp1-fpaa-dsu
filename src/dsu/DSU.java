package dsu;

/**
 * Interface para Disjoint Set Union (DSU) / Union-Find.
 *
 * Define o contrato formal da estrutura de dados de conjuntos disjuntos,
 * conforme apresentado por Cormen et al. em "Introduction to Algorithms",
 * Capítulo 21, Seções 21.1 e 21.3.
 *
 * Modelo:
 * Mantém uma coleção dinâmica de conjuntos disjuntos (não sobrepostos).
 * Cada conjunto é identificado por um representante canônico (um membro qualquer do conjunto).
 * Duas chamadas sucessivas a findSet(x) retornam o mesmo representante,
 * enquanto o conjunto não for modificado.
 *
 * Implementação subjacente:
 * Floresta de árvores enraizadas, onde cada nó aponta para seu pai.
 * A raiz de cada árvore (nó cuja parent[x] == x) é o representante do conjunto.
 *
 * Operações primitivas:
 * - makeSet(x): cria um novo conjunto unitário contendo apenas x
 * - findSet(x): retorna o representante canônico do conjunto contendo x
 * - union(x, y): funde os conjuntos contendo x e y
 *
 * Invariante fundamental:
 * Dois elementos x e y pertencem ao mesmo conjunto se e somente se
 * findSet(x) == findSet(y).
 *
 * Variantes de implementação (Cormen, Seção 21.3):
 * 1. Naive (sem otimizações): findSet em O(n) no pior caso
 * 2. Union by Rank: findSet em O(log n) no pior caso
 * 3. Union by Rank + Path Compression (Full Tarjan): findSet em O(α(n)) amortizado
 *
 * Referência: Cormen et al., "Introduction to Algorithms", Cap. 21, Seções 21.1–21.4
 */
public interface DSU {

    /**
     * Cria um novo conjunto unitário contendo apenas o elemento x.
     * Após a chamada, x é seu próprio representante e seu conjunto contém apenas x.
     *
     * Pré-condição (Cormen, Seção 21.1):
     * x não deve ter sido inicializado em nenhum conjunto anterior.
     * Cada elemento deve ter makeSet chamado exatamente uma vez.
     *
     * Complexidade: O(1) em todas as variantes.
     *
     * @param x índice do elemento (0 ≤ x < capacity)
     */
    void makeSet(int x);

    /**
     * Retorna o representante canônico do conjunto contendo x.
     * Segue os apontadores pai até encontrar a raiz (nó que aponta para si mesmo).
     *
     * Comportamento por variante:
     * - Naive: busca simples pela raiz sem modificar a estrutura. O(n) no pior caso.
     * - Union by Rank: busca simples sem compressão. O(log n) no pior caso.
     * - Full Tarjan: aplica path compression (reaponta para raiz). O(α(n)) amortizado.
     *
     * Propriedade:
     * Se x e y estão no mesmo conjunto, então findSet(x) == findSet(y).
     *
     * @param x índice do elemento
     * @return índice do representante canônico do conjunto que contém x
     */
    int findSet(int x);

    /**
     * Funde os conjuntos contendo os elementos x e y.
     * Após a chamada, todos os elementos dos dois conjuntos originais
     * estão no mesmo conjunto com um único representante.
     *
     * Se x e y já estão no mesmo conjunto, union é um no-op semântico.
     * Se estão em conjuntos distintos, as duas árvores são conectadas
     * de acordo com a estratégia de balanceamento da variante.
     *
     * Comportamento por variante:
     * - Naive: liga uma raiz à outra sem balanceamento. Pode criar cadeias lineares.
     * - Union by Rank: liga árvore menor sob árvore maior. Altura limitada a log(n).
     * - Full Tarjan: union by rank + path compression. Mantém altura logarítmica.
     *
     * Observação:
     * A operação é irreversível — conjuntos disjuntos não suportam separação.
     *
     * Complexidade:
     * Dominada pelos findSet internos. Varia conforme a variante implementada.
     *
     * @param x índice do primeiro elemento
     * @param y índice do segundo elemento
     */
    void union(int x, int y);
}