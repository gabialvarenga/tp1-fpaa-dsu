package tp1.fpaa.output;

import tp1.fpaa.experiment.mst.MSTBenchmarkResult;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

public class MSTBenchmarkCSVExporter implements Closeable {

    private final PrintWriter kruskalWriter;
    private final PrintWriter stressWriter;

    public MSTBenchmarkCSVExporter(String outputDir) throws IOException {
        Files.createDirectories(Paths.get(outputDir));
        kruskalWriter = new PrintWriter(new FileWriter(outputDir + "/kruskal.csv"));
        stressWriter = new PrintWriter(new FileWriter(outputDir + "/stress.csv"));
        String header = "variant,n,median_time_ms,std_time_ms,avg_accesses,std_accesses";
        kruskalWriter.println(header);
        stressWriter.println(header);
    }

    public void writeKruskalRow(MSTBenchmarkResult r) {
        kruskalWriter.printf(Locale.US, "%s,%d,%.6f,%.6f,%.0f,%.0f%n",
                r.getVariant(), r.getN(),
                r.medianTimeMs(), r.stdTimeMs(),
                r.avgAccesses(), r.stdAccesses());
    }

    public void writeStressRow(MSTBenchmarkResult r) {
        stressWriter.printf(Locale.US, "%s,%d,%.6f,%.6f,%.0f,%.0f%n",
                r.getVariant(), r.getN(),
                r.medianTimeMs(), r.stdTimeMs(),
                r.avgAccesses(), r.stdAccesses());
    }

    @Override
    public void close() {
        kruskalWriter.close();
        stressWriter.close();
    }
}
