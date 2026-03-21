package dsu;

/**
 * Implementação de Disjoint Set Union (DSU) / Union-Find com Union by Rank e Path Compression.
 *
 * Descrição:
 * Estrutura de dados que representa uma coleção de conjuntos disjuntos.
 * Cada conjunto é representado como uma árvore, com a raiz sendo o representante.
 * Combina as duas heurísticas clássicas para otimizar as operações:
 * - Union by Rank: mantém as árvores balanceadas
 * - Path Compression: comprime caminhos durante findSet, aproximando altura a 1
 *
 * Operações principais:
 * - makeSet(x): cria um novo conjunto contendo apenas x
 * - findSet(x): retorna o representante (raiz) do conjunto contendo x
 * - union(x, y): une os conjuntos contendo x e y
 * - connected(x, y): verifica se x e y estão no mesmo conjunto
 *
 * Otimizações combinadas (Full Tarjan):
 * Union by Rank: sempre anexa a árvore com menor rank sob a árvore com maior rank.
 * Path Compression: durante findSet, cada nó no caminho é reapontado diretamente para a raiz.
 * A combinação garante que m operações executam em tempo amortizado O(m × α(n)),
 * onde α é a inversa da função de Ackermann — praticamente O(1) para qualquer entrada realista.
 *
 * Referência: Cormen et al., "Introduction to Algorithms", Seções 21.3–21.4
 */
public class DSUFullTarjan implements DSU {

    /**
     * parent[x] = pai do elemento x na árvore.
     * Quando parent[x] == x, x é a raiz (representante do conjunto).
     * Tipo: int[] (suficiente para indexação de até 2 bilhões de elementos)
     *
     * Modificação por Path Compression:
     * Durante findSet, parent[x] é atualizado para apontar diretamente à raiz,
     * drasticamente reduzindo a altura efetiva da árvore em futuras operações.
     */
    private final int[] parent;

    /**
     * rank[x] = aproximação da altura da árvore quando x é uma raiz.
     * Propriedade invariante: rank[x] < rank[parent[x]] para todo nó não-raiz x.
     * Com Path Compression, rank passa a ser um limitante superior da altura real
     * (não exatamente a altura), mas permanece válido para análise.
     * Tipo: byte (suficiente pois rank cresce no máximo log₂(n) ≈ 26 para n = 100 milhões)
     *
     * Observação:
     * Path Compression pode reduzir altura real sem alterar rank.
     * Isso é aceitável: a análise amortizada permanece válida.
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
    public DSUFullTarjan(int n) {
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
     * Segue os apontadores parent até encontrar a raiz.
     * Aplica Path Compression: cada nó no caminho é reapontado diretamente à raiz.
     *
     * Lógica:
     * Se x não é raiz:
     *   1. Recursivamente encontra a raiz do pai de x
     *   2. Atualiza parent[x] para apontar diretamente à raiz (compressão)
     *   3. Retorna a raiz
     *
     * Mecanismo de Path Compression:
     * A operação parent[x] = findSet(parent[x]) é crítica:
     * - Todos os nós no caminho de x até a raiz são comprimidos
     * - Futuras buscas pelo mesmo caminho custam O(1)
     *
     * Exemplo:
     * Antes: a → b → c → d → raiz (cadeia de altura 4)
     * Depois: a, b, c, d apontam diretamente para raiz (altura efetiva = 1)
     *
     * Observação:
     * Com Path Compression, rank deixa de ser altura real e passa a ser
     * um limitante superior, mas a análise amortizada permanece válida.
     * Complexidade amortizada: O(α(n)), onde α é a inversa de Ackermann (praticamente O(1))
     */
    @Override
    public int findSet(int x) {
        if (parent[x] != x) {
            parent[x] = findSet(parent[x]); // COMPRESSÃO: atualiza pai para raiz
        }
        return parent[x];
    }

    /**
     * Une os conjuntos contendo x e y.
     * Localiza as raízes de ambos os conjuntos (com Path Compression ativa)
     * e conecta uma árvore à outra usando Union by Rank.
     *
     * Lógica:
     * 1. Encontra rx = findSet(x) e ry = findSet(y)
     * 2. Se rx != ry, chama link(rx, ry)
     *
     * Observação:
     * A verificação rx != ry é essencial para preservar o invariante de rank.
     * Se ambos forem iguais, os elementos já pertencem ao mesmo conjunto (no-op).
     * Complexidade amortizada: O(α(n))
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
     * Invariante mantido:
     * Para todo nó não-raiz u: rank[u] < rank[parent[u]]
     * Além disso: rank[u] <= ⌊log₂(n)⌋
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
     * Ambos os findSet executam com Path Compression.
     * Complexidade amortizada: O(α(n))
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
     * Útil para depuração: exibe os arrays parent e rank de cada elemento.
     * Se capacity > 20, retorna apenas o tamanho.
     *
     * Complexidade: O(capacity)
     */
    @Override
    public String toString() {
        if (capacity > 20) {
            return "DSUFullTarjan{capacity=" + capacity + "}";
        }
        StringBuilder sb = new StringBuilder("DSUFullTarjan{capacity=")
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