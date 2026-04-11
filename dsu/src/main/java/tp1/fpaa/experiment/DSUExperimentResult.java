package tp1.fpaa.experiment;

public final class DSUExperimentResult {

    private final String experiment;
    private final String variant;
    private final int n;

    private final long pointerAccesses;

    private final double avgPathLength;

    private final int maxHeight;

    private final int theoreticalMaxHeight;

    private final double medianMs;

    private final double nsPerOp;

    private final double speedupVsNaive;

    private final PassResult[] passes;

    public static DSUExperimentResult forE1(int n, long pointerAccesses, double avgPathLength) {
        return new DSUExperimentResult("E1", "Naive", n,
                pointerAccesses, avgPathLength,
                -1, -1, -1, Double.NaN, Double.NaN, null);
    }

    public static DSUExperimentResult forE2(int n, long pointerAccesses, double avgPathLength,
            int maxHeight, int theoreticalMaxHeight) {
        return new DSUExperimentResult("E2", "UnionRank", n,
                pointerAccesses, avgPathLength,
                maxHeight, theoreticalMaxHeight, -1, Double.NaN, Double.NaN, null);
    }

    public static DSUExperimentResult forE3(String variant, int n, double medianMs,
            double nsPerOp, double speedupVsNaive) {
        return new DSUExperimentResult("E3", variant, n,
                -1, Double.NaN,
                -1, -1, medianMs, nsPerOp, speedupVsNaive, null);
    }

    public static DSUExperimentResult forE4(String variant, int n, PassResult[] passes) {
        return new DSUExperimentResult("E4", variant, n,
                -1, Double.NaN,
                -1, -1, -1, Double.NaN, Double.NaN, passes);
    }

    private DSUExperimentResult(String experiment, String variant, int n,
            long pointerAccesses, double avgPathLength,
            int maxHeight, int theoreticalMaxHeight,
            double medianMs, double nsPerOp, double speedupVsNaive,
            PassResult[] passes) {
        this.experiment = experiment;
        this.variant = variant;
        this.n = n;
        this.pointerAccesses = pointerAccesses;
        this.avgPathLength = avgPathLength;
        this.maxHeight = maxHeight;
        this.theoreticalMaxHeight = theoreticalMaxHeight;
        this.medianMs = medianMs;
        this.nsPerOp = nsPerOp;
        this.speedupVsNaive = speedupVsNaive;
        this.passes = passes;
    }

    public String getExperiment() {
        return experiment;
    }

    public String getVariant() {
        return variant;
    }

    public int getN() {
        return n;
    }

    public long getPointerAccesses() {
        return pointerAccesses;
    }

    public double getAvgPathLength() {
        return avgPathLength;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getTheoreticalMaxHeight() {
        return theoreticalMaxHeight;
    }

    public double getMedianMs() {
        return medianMs;
    }

    public double getNsPerOp() {
        return nsPerOp;
    }

    public double getSpeedupVsNaive() {
        return speedupVsNaive;
    }

    public PassResult[] getPasses() {
        return passes;
    }

    public static final class PassResult {
        private final int passNumber;
        private final double passMs;
        private final long pointerAccesses;
        private final double avgPathLength;
        private final int maxDepthAfter;

        public PassResult(int passNumber, double passMs,
                long pointerAccesses, double avgPathLength, int maxDepthAfter) {
            this.passNumber = passNumber;
            this.passMs = passMs;
            this.pointerAccesses = pointerAccesses;
            this.avgPathLength = avgPathLength;
            this.maxDepthAfter = maxDepthAfter;
        }

        public int getPassNumber() {
            return passNumber;
        }

        public double getPassMs() {
            return passMs;
        }

        public long getPointerAccesses() {
            return pointerAccesses;
        }

        public double getAvgPathLength() {
            return avgPathLength;
        }

        public int getMaxDepthAfter() {
            return maxDepthAfter;
        }
    }
}