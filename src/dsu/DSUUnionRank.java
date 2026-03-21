package dsu;

/**
 * Implementação de Disjoint Set Union (DSU) / Union-Find com Union by Rank.
 *
 * Descrição:
 * Estrutura de dados que representa uma coleção de conjuntos disjuntos.
 * Cada conjunto é representado como uma árvore, com a raiz sendo o representante.
 * Utiliza a heurística Union by Rank para manter as árvores balanceadas,
 * reduzindo a altura e melhorando a eficiência das operações.
 *
 * Operações principais:
 * - makeSet(x): cria um novo conjunto contendo apenas x
 * - findSet(x): retorna o representante (raiz) do conjunto contendo x
 * - union(x, y): une os conjuntos contendo x e y
 * - connected(x, y): verifica se x e y estão no mesmo conjunto
 *
 * Otimização:
 * Union by Rank: sempre anexa a árvore com menor rank sob a árvore com maior rank.
 * Isso garante que a altura das árvores permaneça logarítmica em relação ao número de elementos.
 *
 * Importante:
 * Esta implementação não utiliza Path Compression. Portanto, a estrutura da árvore
 * não é modificada durante findSet(), mantendo o rank como uma boa aproximação da altura.
 *
 * Referência: Cormen et al., "Introduction to Algorithms", Seção 21.2 (Union by Rank)
 */
public class DSUUnionRank implements DSU {

    /**
     * parent[x] = pai do elemento x na árvore.
     * Quando parent[x] == x, x é a raiz (representante do conjunto).
     * Tipo: int[] (suficiente para indexação de até 2 bilhões de elementos)
     */
    private final int[] parent;

    /**
     * rank[x] = aproximação da altura da árvore quando x é uma raiz.
     * Propriedade invariante: rank[x] < rank[parent[x]] para todo nó não-raiz x.
     * Tipo: byte (suficiente pois rank cresce no máximo log₂(n) ≈ 26 para n = 100 milhões)
     *
     * Observação:
     * O rank não é exatamente a altura, mas uma cota superior usada para balanceamento.
     * Após union by rank, rank[x] >= altura[x].
     */
    private final byte[] rank;

    /**
     * Capacidade máxima de elementos que a estrutura pode armazenar.
     * Elementos válidos: 0 até capacity - 1.
     */
    private final int capacity;

    /**
     * Cria uma estrutura DSU com espaço para n elementos.
     * Nenhum conjunto é criado automaticamente; cada elemento deve ser
     * inicializado com makeSet(x) antes de ser utilizado.
     *
     * Pré-condição: 1 <= n <= 100.000.000
     * Complexidade: O(n)
     */
    public DSUUnionRank(int n) {
        if (n <= 0 || n > 100_000_000) {
            throw new IllegalArgumentException(
                    "Capacidade deve estar em [1, 100000000]. Recebido: " + n);
        }

        this.capacity = n;
        this.parent = new int[n];
        this.rank = new byte[n];
    }

    /**
     * Cria um novo conjunto contendo apenas o elemento x.
     * Após a chamada, x é a raiz de seu próprio conjunto com rank 0.
     *
     * Lógica: parent[x] = x, rank[x] = 0
     * Complexidade: O(1)
     */
    @Override
    public void makeSet(int x) {
        parent[x] = x;
        rank[x] = 0;
    }

    /**
     * Retorna o representante (raiz) do conjunto contendo x.
     * Segue os apontadores parent até encontrar um elemento que aponta para si mesmo.
     *
     * Lógica: sobe a árvore até encontrar a raiz.
     * Observação: não utiliza path compression; a estrutura da árvore permanece inalterada.
     * Complexidade: O(log n) com union by rank
     */
    @Override
    public int findSet(int x) {
        if (parent[x] != x) {
            return findSet(parent[x]);
        }
        return x;
    }

    /**
     * Une os conjuntos contendo x e y.
     * Localiza as raízes de ambos os conjuntos e conecta uma árvore à outra
     * utilizando a heurística union by rank.
     *
     * Lógica:
     * 1. Encontra rx = findSet(x) e ry = findSet(y)
     * 2. Se rx != ry, chama link(rx, ry)
     *
     * Observação:
     * A verificação rx != ry evita que uma árvore seja conectada a si mesma,
     * o que poderia corromper o invariante de rank.
     * Complexidade: O(log n) com union by rank
     */
    @Override
    public void union(int x, int y) {
        int rx = findSet(x);
        int ry = findSet(y);

        if (rx != ry) {
            link(rx, ry);
        }
    }

    /**
     * Conecta duas árvores disjuntas utilizando union by rank.
     * Sempre anexa a árvore com menor rank sob a árvore com maior rank.
     * Se os ranks são iguais, escolhe ry como nova raiz e incrementa seu rank.
     *
     * Lógica:
     * - Se rank[x] > rank[y]: parent[y] = x (y vira filho de x)
     * - Senão: parent[x] = y (x vira filho de y)
     *   - Se rank[x] == rank[y]: rank[y]++ (nova raiz mais profunda)
     *
     * Pré-condição: x e y devem ser raízes (representantes de conjuntos disjuntos)
     * Observação: método privado, chamado apenas por union()
     * Complexidade: O(1)
     */
    private void link(int x, int y) {
        if (rank[x] > rank[y]) {
            parent[y] = x;
        } else {
            parent[x] = y;
            if (rank[x] == rank[y]) {
                rank[y] = (byte) (rank[y] + 1);
            }
        }
    }

    /**
     * Verifica se x e y pertencem ao mesmo conjunto.
     *
     * Lógica: retorna verdadeiro se findSet(x) == findSet(y).
     * Complexidade: O(log n) com union by rank
     */
    public boolean connected(int x, int y) {
        return findSet(x) == findSet(y);
    }

    /**
     * Retorna a capacidade máxima de elementos desta estrutura.
     * Complexidade: O(1)
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Retorna uma representação em string do estado interno da estrutura.
     * Útil para depuração: exibe o array parent e o array rank de cada elemento.
     * Se capacity > 20, retorna apenas o tamanho.
     *
     * Complexidade: O(capacity)
     */
    @Override
    public String toString() {
        if (capacity > 20) {
            return "DSUUnionRank{capacity=" + capacity + "}";
        }
        StringBuilder sb = new StringBuilder("DSUUnionRank{capacity=")
                .append(capacity).append(", parent=[");
        for (int i = 0; i < capacity; i++) {
            sb.append(parent[i]);
            if (i < capacity - 1)
                sb.append(", ");
        }
        sb.append("], rank=[");
        for (int i = 0; i < capacity; i++) {
            sb.append(rank[i]);
            if (i < capacity - 1)
                sb.append(", ");
        }
        return sb.append("]}").toString();
    }
}