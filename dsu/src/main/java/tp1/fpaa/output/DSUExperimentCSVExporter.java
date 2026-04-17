package tp1.fpaa.output;

import tp1.fpaa.experiment.dsuCase.DSUExperimentResult;
import tp1.fpaa.experiment.dsuCase.DSUExperimentResult.PassResult;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DSUExperimentCSVExporter implements Closeable {

    private final PrintWriter e1Writer;
    private final PrintWriter e2Writer;
    private final PrintWriter e3Writer;
    private final PrintWriter e4Writer;

    public DSUExperimentCSVExporter(String outputDir) throws IOException {
        Files.createDirectories(Paths.get(outputDir));
        e1Writer = new PrintWriter(new FileWriter(outputDir + "/e1.csv"));
        e2Writer = new PrintWriter(new FileWriter(outputDir + "/e2.csv"));
        e3Writer = new PrintWriter(new FileWriter(outputDir + "/e3.csv"));
        e4Writer = new PrintWriter(new FileWriter(outputDir + "/e4.csv"));
        e1Writer.println("n,pointer_accesses,avg_path_length");
        e2Writer.println("n,max_height,theoretical_max_height,avg_path_length");
        e3Writer.println("variant,n,median_ms,ns_per_op,speedup_vs_naive,speedup_vs_union_rank");
        e4Writer.println("variant,n,pass,time_ms,max_depth_after,avg_path_length");
    }

    public void writeE1Row(DSUExperimentResult r) {
        e1Writer.printf("%d,%d,%.1f%n",
                r.getN(), r.getPointerAccesses(), r.getAvgPathLength());
    }

    public void writeE2Row(DSUExperimentResult r) {
        e2Writer.printf("%d,%d,%d,%.2f%n",
                r.getN(), r.getMaxHeight(), r.getTheoreticalMaxHeight(), r.getAvgPathLength());
    }

    public void writeE3Row(DSUExperimentResult r) {
        String speedupNaive = Double.isNaN(r.getSpeedupVsNaive())
                ? "" : String.format("%.2f", r.getSpeedupVsNaive());
        String speedupUR = Double.isNaN(r.getSpeedupVsUnionRank())
                ? "" : String.format("%.2f", r.getSpeedupVsUnionRank());
        e3Writer.printf("%s,%d,%.6f,%.1f,%s,%s%n",
                r.getVariant(), r.getN(), r.getMedianMs(), r.getNsPerOp(),
                speedupNaive, speedupUR);
    }

    public void writeE4Row(DSUExperimentResult r) {
        for (PassResult p : r.getPasses()) {
            e4Writer.printf("%s,%d,%d,%.6f,%d,%.2f%n",
                    r.getVariant(), r.getN(), p.getPassNumber(),
                    p.getPassMs(), p.getMaxDepthAfter(), p.getAvgPathLength());
        }
    }

    @Override
    public void close() {
        e1Writer.close();
        e2Writer.close();
        e3Writer.close();
        e4Writer.close();
    }
}
