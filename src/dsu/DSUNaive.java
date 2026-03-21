package dsu;

/**
 * Implementação simples (naive) de Disjoint Set Union (DSU) / Union-Find.
 *
 * Descrição:
 * Estrutura de dados que representa uma coleção de conjuntos disjuntos (não sobrepostos).
 * Cada conjunto é representado como uma árvore, com a raiz sendo o representante do conjunto.
 * Esta versão não utiliza otimizações, servindo como base para entender estruturas mais eficientes.
 *
 * Operações principais:
 * - makeSet(x): cria um novo conjunto contendo apenas x
 * - findSet(x): retorna o representante (raiz) do conjunto contendo x
 * - union(x, y): une os conjuntos contendo x e y
 * - connected(x, y): verifica se x e y estão no mesmo conjunto
 *
 * Referência: Cormen et al., "Introduction to Algorithms", Seção 21.1
 */
public class DSUNaive implements DSU {

    /**
     * parent[x] = pai do elemento x na árvore.
     * Quando parent[x] == x, x é a raiz (representante do conjunto).
     * Tipo: int[] (suficiente para indexação de até 2 bilhões de elementos)
     */
    private final int[] parent;

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
     * Pré-condição: n >= 1
     * Complexidade: O(n)
     */
    public DSUNaive(int n) {
        if (n < 1) {
            throw new IllegalArgumentException(
                    "Capacidade deve ser positiva. Recebido: " + n);
        }
        this.capacity = n;
        this.parent = new int[n];
    }

    /**
     * Cria um novo conjunto contendo apenas o elemento x.
     * Após a chamada, x é a raiz de seu próprio conjunto.
     *
     * Lógica: parent[x] = x
     * Complexidade: O(1)
     */
    @Override
    public void makeSet(int x) {
        parent[x] = x;
    }

    /**
     * Retorna o representante (raiz) do conjunto contendo x.
     * Segue os apontadores parent até encontrar um elemento que aponta para si mesmo.
     *
     * Lógica: sobe a árvore até encontrar a raiz.
     * Observação: não utiliza path compression.
     * Complexidade: O(h), onde h é a altura da árvore (até O(n) no pior caso).
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
     * Localiza as raízes de ambos os conjuntos e conecta uma árvore à outra.
     *
     * Lógica:
     * 1. Encontra rx = findSet(x) e ry = findSet(y)
     * 2. Se rx != ry, faz parent[rx] = ry
     *
     * Observação: não utiliza heurística de balanceamento (union by rank/size).
     * As árvores resultantes podem ser desbalanceadas.
     * Complexidade: O(h), onde h é a altura das árvores (até O(n) no pior caso).
     */
    @Override
    public void union(int x, int y) {
        int rx = findSet(x);
        int ry = findSet(y);

        if (rx != ry) {
            parent[rx] = ry;
        }
    }

    /**
     * Verifica se x e y pertencem ao mesmo conjunto.
     *
     * Lógica: retorna verdadeiro se findSet(x) == findSet(y).
     * Complexidade: O(h), onde h é a altura das árvores (até O(n) no pior caso).
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
     * Útil para depuração: exibe o array parent de cada elemento.
     * Se capacity > 20, retorna apenas o tamanho.
     *
     * Complexidade: O(capacity)
     */
    @Override
    public String toString() {
        if (capacity > 20) {
            return "DSUNaive{capacity=" + capacity + "}";
        }

        StringBuilder sb = new StringBuilder("DSUNaive{capacity=")
                .append(capacity).append(", parent=[");

        for (int i = 0; i < capacity; i++) {
            sb.append(parent[i]);
            if (i < capacity - 1)
                sb.append(", ");
        }

        return sb.append("]}").toString();
    }
}