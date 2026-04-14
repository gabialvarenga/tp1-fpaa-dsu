package tp1.fpaa;

public class EnvironmentInfo {

    public static void printEnvironmentInfo() {

        System.out.println("Ambiente de Testes:");

        System.out.println("Processador: " + System.getenv("PROCESSOR_IDENTIFIER"));

        System.out.println("Número de Núcleos: " +
                Runtime.getRuntime().availableProcessors());

        System.out.println("Heap Máximo (MB): " +
                Runtime.getRuntime().maxMemory() / (1024 * 1024));

        System.out.println("Heap Atual (MB): " +
                Runtime.getRuntime().totalMemory() / (1024 * 1024));

        System.out.println("Sistema Operacional: " +
                System.getProperty("os.name") + " " +
                System.getProperty("os.version"));

        System.out.println("Versão da JVM: " +
                System.getProperty("java.version"));

        System.out.println("Configuração esperada: -Xmx2G");

        System.out.println("MAX_N_DSU: " + getSafeMaxN());
    }

    private static int getSafeMaxN() {
        long heap = Runtime.getRuntime().maxMemory();

        long safe = heap / 2;

        long max = safe / 8;

        return (int) Math.min(max, 25_000_000);
    }
}