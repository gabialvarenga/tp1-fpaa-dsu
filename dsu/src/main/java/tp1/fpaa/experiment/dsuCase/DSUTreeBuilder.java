package tp1.fpaa.experiment.dsuCase;

import tp1.fpaa.algorithm.dsu.DSU;

public final class DSUTreeBuilder {

    private DSUTreeBuilder() {
    }

    public static void buildMaxHeightTree(DSU dsu, int n) {
        int[] roots = createInitialRoots(n);

        int activeRoots = n;

        while (activeRoots > 1) {
            roots = mergeLevel(dsu, roots, activeRoots);
            activeRoots = roots.length;
        }
    }

    private static int[] createInitialRoots(int n) {
        int[] roots = new int[n];
        for (int i = 0; i < n; i++) {
            roots[i] = i;
        }
        return roots;
    }

    private static int[] mergeLevel(DSU dsu, int[] roots, int activeRoots) {
        int newSize = (activeRoots + 1) / 2;
        int[] nextRoots = new int[newSize];

        int idx = 0;

        for (int j = 0; j < activeRoots / 2; j++) {
            int u = roots[2 * j];
            int v = roots[2 * j + 1];

            dsu.union(u, v);

            nextRoots[idx++] = dsu.findSet(u);
        }

        if (activeRoots % 2 == 1) {
            nextRoots[idx] = roots[activeRoots - 1];
        }

        return nextRoots;
    }
}