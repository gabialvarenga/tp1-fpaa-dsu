package dsu;

public class QuickFindDSU implements DSU {

    private int[] parent;
    private long acessos_parent = 0;
    private long acessos_rank = 0;

    public void reset(int n) {
        parent = new int[n];
        for(int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    public int find(int x) {
        acessos_parent++;
        return parent[x];
    }

    public void union_sets(int a, int b) {
        int pa = find(a);
        int pb = find(b);

        for(int i = 0; i < parent.length; i++) {
            acessos_parent++;
            if(parent[i] == pa) {
                parent[i] = pb;
            }
        }
    }

    public long get_acessos_parent() {
        return acessos_parent;
    }

    public long get_acessos_rank() {
        return acessos_rank;
    }
}