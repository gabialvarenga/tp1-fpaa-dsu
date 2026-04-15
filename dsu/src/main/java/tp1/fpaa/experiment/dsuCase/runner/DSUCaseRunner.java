package tp1.fpaa.experiment.dsuCase.runner;

import tp1.fpaa.experiment.dsuCase.DSUExperimentResult;

public final class DSUCaseRunner {
    private DSUCaseRunner() {
    }

    public static DSUExperimentResult[] runE1(int[] sizes) {
        return E1Runner.run(sizes);
    }

    public static DSUExperimentResult[] runE2(int[] sizes) {
        return E2Runner.run(sizes);
    }

    public static DSUExperimentResult[] runE3(int[] sizes) {
        return E3Runner.run(sizes);
    }

    public static DSUExperimentResult[] runE4(int[] sizes) {
        return E4Runner.run(sizes);
    }

}