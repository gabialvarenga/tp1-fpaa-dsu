package tp1.fpaa.output;

import tp1.fpaa.experiment.DSUExperimentResult;
import tp1.fpaa.experiment.DSUExperimentResult.PassResult;

public final class DSUExperimentPrinter {

    private static final int LINE_WIDTH = 84;
    private static final String SEP = "-".repeat(LINE_WIDTH);

    private void printHeader(String title, String subtitle, String note) {
        System.out.println("=".repeat(LINE_WIDTH));
        System.out.println("  " + title);
        System.out.println("  " + subtitle);
        System.out.println("  " + note);
        System.out.println("=".repeat(LINE_WIDTH));
    }


    public void printE1Header() {
        System.out.println();
        printHeader(
            "Experimento E1 - Pior caso do DSU Naive (Cadeia linear)",
            "Cria cadeia de n nos e realiza n Finds iterativos no no mais distante da base",
            "Expectativa teorica: O(n) acessos por Find -> O(n^2) total | Testado ate 50.000 nos"
        );
        System.out.printf("| %-14s | %30s | %30s |%n",
                "n", "Acessos Totais", "Nos por Find");
        System.out.println(SEP);
    }

    public void printE1Row(DSUExperimentResult r) {
        System.out.printf("| %-14d | %30d | %30.1f |%n",
                r.getN(), r.getPointerAccesses(), r.getAvgPathLength());
    }

    public void printE1Footer() {
        System.out.println(SEP);
    }


    public void printE2Header() {
        System.out.println();
        printHeader(
            "Experimento E2 - Union by Rank (sem compressao)",
            "Forca arvore com maior altura possivel e faz 10.000 Finds no no mais profundo",
            "Expectativa teorica: Numero de nos por Find deve se aproximar de log2(n) | Testado ate 16.777.216 nos"
        );
        System.out.printf("| %-14s | %20s | %20s | %20s |%n",
                "n", "Altura Real", "Alt. Esperada", "Nos por Find");
        System.out.println(SEP);
    }

    public void printE2Row(DSUExperimentResult r) {
        System.out.printf("| %-14d | %20d | %20d | %20.2f |%n",
                r.getN(), r.getMaxHeight(), r.getTheoreticalMaxHeight(), r.getAvgPathLength());
    }

    public void printE2Footer() {
        System.out.println(SEP);
    }


    public void printE3Header() {
        System.out.println();
        printHeader(
            "Experimento E3 - Desempenho com operacoes mistas (Benchmark)",
            "Carga: 60% uniao, 40% find | Repeticoes: 5 (exibindo a mediana)",
            "Naive: ate 50.000 nos | UnionRank e FullTarjan: ate 5.000.000 nos"
        );
        System.out.printf("| %-11s | %10s | %15s | %16s | %16s |%n",
                "Variante", "n", "Tempo Med.(ms)", "ns / Operacao", "Ganho vs Naive");
        System.out.println(SEP);
    }

    public void printE3Row(DSUExperimentResult r) {
        String speedup = Double.isNaN(r.getSpeedupVsNaive())
                ? "N/A"
                : String.format("%.1fx", r.getSpeedupVsNaive());
        System.out.printf("| %-11s | %10d | %15d | %16.1f | %16s |%n",
                r.getVariant(), r.getN(), r.getMedianMs(), r.getNsPerOp(), speedup);
    }

    public void printE3Separator() {
        System.out.println(SEP);
    }

    public void printE3Footer() {
        System.out.println(SEP);
    }


    public void printE4Header() {
        System.out.println();
        printHeader(
            "Experimento E4 - Efeito isolado da Compressao de Caminho",
            "Mede o custo de n Finds ao longo de 3 passagens em uma arvore profunda",
            "Otimizado: custo cai na 2a passagem | Sem compressao: custo constante | Testado ate 4.194.304 nos"
        );
        System.out.printf("| %-11s | %8s | %8s | %10s | %11s | %17s |%n",
                "Variante", "n", "Passagem", "Tempo(ms)", "Altura Max", "Nos por Find");
        System.out.println(SEP);
    }

    public void printE4Result(DSUExperimentResult r) {
        for (PassResult p : r.getPasses()) {
            System.out.printf("| %-11s | %8d | %8d | %10d | %11d | %17.2f |%n",
                    r.getVariant(), r.getN(), p.getPassNumber(), p.getPassMs(),
                    p.getMaxDepthAfter(), p.getAvgPathLength());
        }
    }

    public void printE4Separator() {
        System.out.println(SEP);
    }

    public void printE4Footer() {
        System.out.println(SEP);
    }


    public void printSeparator() {
        System.out.println(SEP);
    }
}