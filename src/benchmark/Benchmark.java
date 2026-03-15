package benchmark;

import dsu.*;

public class Benchmark {

    public static void main(String[] args) {

        int n = 1000;

        DSU dsu = new QuickFindDSU();
        dsu.reset(n);

        long start = System.nanoTime();

        for(int i = 0; i < n-1; i++) {
            dsu.union_sets(i, i+1);
        }

        long end = System.nanoTime();

        System.out.println("Tempo: " + (end-start));
    }
}