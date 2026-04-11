package tp1.fpaa;

import tp1.fpaa.algorithm.mst.Edge;
import tp1.fpaa.experiment.DSUCaseRunner;
import tp1.fpaa.experiment.DSUExperimentResult;
import tp1.fpaa.experiment.MSTBenchmarkRunner;
import tp1.fpaa.experiment.RandomConnectedGraphFactory;
import tp1.fpaa.output.DSUExperimentPrinter;
import tp1.fpaa.output.MSTBenchmarkPrinter;

public class Main {

    private static final int[] SIZES = {
            500, 1_000, 5_000, 10_000, 50_000, 100_000, 500_000, 1_000_000,1_500_000
    };

    private static final int REPETITIONS = 11;
    private static final long SEED = 42L;
    private static final int QUERY_MULTIPLIER = 50;

    public static void main(String[] args) {

        RandomConnectedGraphFactory factory = new RandomConnectedGraphFactory(SEED);
        MSTBenchmarkRunner runner = new MSTBenchmarkRunner(REPETITIONS, SEED, QUERY_MULTIPLIER);
        MSTBenchmarkPrinter printer = new MSTBenchmarkPrinter();

        printer.printKruskalHeader(REPETITIONS, SEED);

        for (int n : SIZES) {
            Edge[] edges = factory.generate(n, factory.sparseEdgeCount(n));

            if (n <= 100_000) {
                printer.printRow(runner.runKruskal("Naive", n, edges));
            }
            printer.printRow(runner.runKruskal("UnionRank", n, edges));
            printer.printRow(runner.runKruskal("FullTarjan", n, edges));
            printer.printSeparator();
        }

        printer.printStressHeader(QUERY_MULTIPLIER, REPETITIONS, SEED);

        for (int n : SIZES) {
            Edge[] edges = factory.generate(n, factory.sparseEdgeCount(n));

            printer.printRow(runner.runStress("UnionRank", n, edges));
            printer.printRow(runner.runStress("FullTarjan", n, edges));
            printer.printSeparator();
        }

        DSUExperimentPrinter ap = new DSUExperimentPrinter();
        int[] e1Sizes = { 1_000, 5_000, 10_000, 20_000, 50_000,100_000};
        ap.printE1Header();
        for (DSUExperimentResult r : DSUCaseRunner.runE1(e1Sizes)) {
            ap.printE1Row(r);
        }
        ap.printE1Footer();

        int[] e2Sizes = { 1_024, 4_096, 16_384, 65_536, 262_144, 1_048_576, 4_194_304, 16_777_216 };
        ap.printE2Header();
        for (DSUExperimentResult r : DSUCaseRunner.runE2(e2Sizes)) {
            ap.printE2Row(r);
        }
        ap.printE2Footer();

        int[] e3Sizes = { 1_000, 5_000, 10_000, 50_000, 100_000, 500_000, 1_000_000, 2_000_000, 5_000_000 };
        ap.printE3Header();
        int prevN = -1;
        for (DSUExperimentResult r : DSUCaseRunner.runE3(e3Sizes)) {
            if (prevN != -1 && r.getN() != prevN)
                ap.printE3Separator();
            ap.printE3Row(r);
            prevN = r.getN();
        }
        ap.printE3Footer();

        int[] e4Sizes = { 1_024, 16_384, 262_144, 1_048_576, 4_194_304 };
        ap.printE4Header();
        int prevN4 = -1;
        for (DSUExperimentResult r : DSUCaseRunner.runE4(e4Sizes)) {
            if (prevN4 != -1 && r.getN() != prevN4)
                ap.printE4Separator();
            ap.printE4Result(r);
            prevN4 = r.getN();
        }
        ap.printE4Footer();
    }
}