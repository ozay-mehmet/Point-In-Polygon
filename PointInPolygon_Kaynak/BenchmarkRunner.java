import java.util.List;

public class BenchmarkRunner {

    // JIT derlemeyi etkisiz kılmak için ısınma turları
    private static final int WARMUP_ROUNDS   = 3;
    // Gerçek ölçüm turları (ortalama alınır)
    private static final int MEASURE_ROUNDS  = 5;
    // Koordinat aralığı (poligon ~10 birim genişliğinde, bu yüzden 15 alıyoruz)
    private static final double COORD_RANGE  = 15.0;

    // Test edilecek nokta sayıları
    private static final int[] POINT_COUNTS = {100, 500, 1000, 5000, 10_000, 50_000};

    // Test edilecek thread sayıları
    private static final int[] THREAD_COUNTS;
    static {
        int cpus = Runtime.getRuntime().availableProcessors();
        THREAD_COUNTS = new int[]{1, 2, 4, Math.min(8, cpus)};
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   NOKTA-İÇİNDE-POLİGON — PARALEL BENCHMARK           ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.printf("  Sistem: %d CPU çekirdeği%n", Runtime.getRuntime().availableProcessors());
        System.out.printf("  Ölçüm : %d ısınma + %d gerçek tur%n%n", WARMUP_ROUNDS, MEASURE_ROUNDS);

        // Concave (yıldız) poligon kullan
        List<Point> polygon = PolygonUtils.createConcavePolygon();
        PolygonUtils.printPolygon(polygon, "Test Poligonu (Concave Yıldız)");

        System.out.println();
        System.out.printf("%-10s | %-12s | %-12s | %-12s | %-10s%n",
                "Nokta", "T.Sayısı", "Sıralı(ms)", "Paralel(ms)", "Speedup");
        System.out.println("-".repeat(65));

        for (int pointCount : POINT_COUNTS) {
            List<Point> points = PolygonUtils.generateRandomPoints(pointCount, COORD_RANGE, 42L);

            // Sıralı ölçüm
            double seqMs = measureSequential(polygon, points);

            for (int threadCount : THREAD_COUNTS) {
                // Birden fazla thread sayısı varsa sadece benzersiz olanları göster
                double parMs  = measureParallel(polygon, points, threadCount);
                double speedup = seqMs / parMs;

                System.out.printf("%-10d | %-12d | %-12.3f | %-12.3f | %.3fx%n",
                        pointCount, threadCount, seqMs, parMs, speedup);
            }
            System.out.println();
        }

        System.out.println("Benchmark tamamlandı.");
    }

    private static double measureSequential(List<Point> polygon, List<Point> points) {
        SequentialSolver solver = new SequentialSolver(polygon);

        // Isınma
        for (int i = 0; i < WARMUP_ROUNDS; i++) solver.solve(points);

        // Gerçek ölçüm
        long total = 0;
        for (int i = 0; i < MEASURE_ROUNDS; i++) {
            solver.solve(points);
            total += solver.getLastElapsedNs();
        }
        return (total / (double) MEASURE_ROUNDS) / 1_000_000.0;
    }

    private static double measureParallel(List<Point> polygon, List<Point> points,
                                           int threadCount) {
        ParallelSolver solver = new ParallelSolver(polygon, threadCount);

        // Isınma
        for (int i = 0; i < WARMUP_ROUNDS; i++) solver.solve(points);

        // Gerçek ölçüm
        long total = 0;
        for (int i = 0; i < MEASURE_ROUNDS; i++) {
            solver.solve(points);
            total += solver.getLastElapsedNs();
        }
        solver.shutdown();
        return (total / (double) MEASURE_ROUNDS) / 1_000_000.0;
    }
}
