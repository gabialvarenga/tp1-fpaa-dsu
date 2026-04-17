package tp1.fpaa;

import tp1.fpaa.algorithm.mst.Edge;
import tp1.fpaa.experiment.dsuCase.DSUExperimentResult;
import tp1.fpaa.experiment.dsuCase.runner.DSUCaseRunner;
import tp1.fpaa.experiment.mst.MSTBenchmarkResult;
import tp1.fpaa.experiment.mst.MSTBenchmarkRunner;
import tp1.fpaa.experiment.mst.RandomConnectedGraphFactory;
import tp1.fpaa.output.DSUExperimentCSVExporter;
import tp1.fpaa.output.DSUExperimentPrinter;
import tp1.fpaa.output.MSTBenchmarkCSVExporter;
import tp1.fpaa.output.MSTBenchmarkPrinter;

import java.io.IOException;

public class Main {

    private static final int[] SIZES = {
            500, 1_000, 5_000, 10_000, 50_000, 100_000, 500_000, 1_000_000, 1_500_000, 2_000_000, 5_000_000,
    };

    private static final int REPETITIONS = 11;
    private static final long SEED = 42L;
    private static final int QUERY_MULTIPLIER = 50;

    private static final String CSV_OUTPUT_DIR = "../data/results";

    public static void main(String[] args) {
        EnvironmentInfo.printEnvironmentInfo();
        RandomConnectedGraphFactory factory = new RandomConnectedGraphFactory(SEED);
        MSTBenchmarkRunner runner = new MSTBenchmarkRunner(REPETITIONS, SEED, QUERY_MULTIPLIER);
        MSTBenchmarkPrinter printer = new MSTBenchmarkPrinter();
        DSUExperimentPrinter ap = new DSUExperimentPrinter();

        try (MSTBenchmarkCSVExporter mstCsv = new MSTBenchmarkCSVExporter(CSV_OUTPUT_DIR);
             DSUExperimentCSVExporter dsuCsv = new DSUExperimentCSVExporter(CSV_OUTPUT_DIR)) {

            printer.printKruskalHeader(REPETITIONS, SEED);
            for (int n : SIZES) {
                Edge[] edges = factory.generate(n, factory.sparseEdgeCount(n));
                if (n <= 100_000) {
                    MSTBenchmarkResult naive = runner.runKruskal("Naive", n, edges);
                    printer.printRow(naive);
                    mstCsv.writeKruskalRow(naive);
                }
                MSTBenchmarkResult ur = runner.runKruskal("UnionRank", n, edges);
                MSTBenchmarkResult ft = runner.runKruskal("FullTarjan", n, edges);
                printer.printRow(ur);
                printer.printRow(ft);
                mstCsv.writeKruskalRow(ur);
                mstCsv.writeKruskalRow(ft);
                printer.printSeparator();
            }

            printer.printStressHeader(QUERY_MULTIPLIER, REPETITIONS, SEED);
            for (int n : SIZES) {
                Edge[] edges = factory.generate(n, factory.sparseEdgeCount(n));
                MSTBenchmarkResult ur = runner.runStress("UnionRank", n, edges);
                MSTBenchmarkResult ft = runner.runStress("FullTarjan", n, edges);
                printer.printRow(ur);
                printer.printRow(ft);
                mstCsv.writeStressRow(ur);
                mstCsv.writeStressRow(ft);
                printer.printSeparator();
            }

            int[] e1Sizes = { 1_000, 5_000, 10_000, 20_000, 50_000, 100_000 };
            ap.printE1Header();
            for (DSUExperimentResult r : DSUCaseRunner.runE1(e1Sizes)) {
                ap.printE1Row(r);
                dsuCsv.writeE1Row(r);
            }
            ap.printE1Footer();

            int[] e2Sizes = { 1_024, 4_096, 16_384, 65_536, 262_144, 1_048_576, 4_194_304, 16_777_216 };
            ap.printE2Header();
            for (DSUExperimentResult r : DSUCaseRunner.runE2(e2Sizes)) {
                ap.printE2Row(r);
                dsuCsv.writeE2Row(r);
            }
            ap.printE2Footer();

            int[] e3Sizes = { 1_000, 5_000, 10_000, 50_000, 100_000, 500_000, 1_000_000, 2_000_000, 5_000_000 };
            DSUExperimentResult[] e3Results = DSUCaseRunner.runE3(e3Sizes);

            ap.printE3Header();
            int prevN = -1;
            for (DSUExperimentResult r : e3Results) {
                if (prevN != -1 && r.getN() != prevN) ap.printE3Separator();
                ap.printE3Row(r);
                dsuCsv.writeE3Row(r);
                prevN = r.getN();
            }
            ap.printE3Footer();

            ap.printE3UnionRankBaselineHeader();
            int prevN2 = -1;
            for (DSUExperimentResult r : e3Results) {
                if (r.getVariant().equals("Naive")) continue;
                if (prevN2 != -1 && r.getN() != prevN2) ap.printE3UnionRankBaselineSeparator();
                ap.printE3UnionRankBaselineRow(r);
                prevN2 = r.getN();
            }
            ap.printE3UnionRankBaselineFooter();

            int[] e4Sizes = { 1_024, 16_384, 262_144, 1_048_576, 4_194_304 };
            ap.printE4Header();
            int prevN4 = -1;
            for (DSUExperimentResult r : DSUCaseRunner.runE4(e4Sizes)) {
                if (prevN4 != -1 && r.getN() != prevN4) ap.printE4Separator();
                ap.printE4Result(r);
                dsuCsv.writeE4Row(r);
                prevN4 = r.getN();
            }
            ap.printE4Footer();

        } catch (IOException e) {
            System.err.println("Erro ao exportar CSVs: " + e.getMessage());
        }
    }
}
