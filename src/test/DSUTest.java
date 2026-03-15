package test;

import dsu.*;

public class DSUTest {

    public static void main(String[] args) {

        DSU dsu = new QuickFindDSU();
        dsu.reset(5);

        dsu.union_sets(1,2);

        if(dsu.find(1) == dsu.find(2)) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL");
        }
    }
}
