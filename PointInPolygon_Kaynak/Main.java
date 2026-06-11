import java.util.List;

public class Main {

    public static void main(String[] args) {
        int cpus = Runtime.getRuntime().availableProcessors();
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  NOKTA-İÇİNDE-POLİGON — PARALEL ÇÖZÜM DEMOsu          ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.printf("  Kullanılabilir CPU çekirdeği: %d%n%n", cpus);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  BÖLÜM 1: Convex Poligon (10 kenarlı)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        List<Point> convex = PolygonUtils.createConvexPolygon(0, 0, 5.0, 10);
        PolygonUtils.printPolygon(convex, "Convex Ongen");

        // Elle tanımlanmış test noktaları
        List<Point> testPoints = List.of(
            new Point( 0.0,  0.0),   // Merkez → İçeride
            new Point( 4.5,  0.0),   // Kenara yakın → İçeride
            new Point( 5.5,  0.0),   // Dışında
            new Point(-4.5,  0.0),   // İçeride
            new Point( 0.0,  4.9),   // İçeride (üst)
            new Point( 0.0,  5.1),   // Dışında (az çıkmış)
            new Point( 3.5,  3.5),   // Köşede yakın → sınır bölgesi
            new Point(-6.0, -6.0),   // Tamamen dışında
            new Point( 2.0, -2.0),   // İçeride
            new Point( 0.0,  0.0)    // Merkez tekrar → İçeride
        );

        runDemo(convex, testPoints, cpus);

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  BÖLÜM 2: Concave Poligon (6 kollu Yıldız)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        List<Point> concave = PolygonUtils.createConcavePolygon();
        PolygonUtils.printPolygon(concave, "Concave Yıldız");

        List<Point> testPoints2 = List.of(
            new Point( 0.0,  0.0),   // Merkez → İçeride
            new Point( 0.0,  9.5),   // Üst kol ucu → İçeride
            new Point( 0.0,  6.0),   // Kol içi → İçeride
            new Point( 4.0,  4.0),   // Girintide → Dışarıda
            new Point( 1.0,  1.0),   // Merkez yakın → İçeride
            new Point( 8.0,  8.0),   // Uzakta → Dışarıda
            new Point(-5.0,  0.0),   // Sol kol → İçeride
            new Point(-3.5,  2.0),   // Girinti köşesi → Dışarıda
            new Point( 0.0, -9.5),   // Alt kol → İçeride
            new Point( 3.0, -3.0)    // Girintide → Dışarıda
        );

        runDemo(concave, testPoints2, cpus);

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  BÖLÜM 3: Hızlanma Katsayısı — 1000 Nokta");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        showSpeedup(concave, 1_000, cpus);
    }

    private static void runDemo(List<Point> polygon, List<Point> points, int cpus) {
        System.out.println("\n  Test Noktaları ve Sonuçları:");
        System.out.printf("  %-22s | %-12s | %-12s%n", "Nokta", "Sıralı", "Paralel");
        System.out.println("  " + "-".repeat(50));

        SequentialSolver seq = new SequentialSolver(polygon);
        ParallelSolver   par = new ParallelSolver(polygon, cpus);

        boolean[] seqResults = seq.solve(points);
        boolean[] parResults = par.solve(points);

        boolean allMatch = true;
        for (int i = 0; i < points.size(); i++) {
            String seqStr = seqResults[i] ? "✓ İÇERİDE" : "✗ DIŞARIDA";
            String parStr = parResults[i] ? "✓ İÇERİDE" : "✗ DIŞARIDA";
            String match  = seqResults[i] == parResults[i] ? "" : "  ← UYUŞMAZLIK!";
            if (!match.isEmpty()) allMatch = false;
            System.out.printf("  %-22s | %-12s | %-12s%s%n",
                    points.get(i), seqStr, parStr, match);
        }

        System.out.println();
        System.out.println("  Sonuç doğrulaması: " + (allMatch ? "✓ Tüm sonuçlar eşleşiyor" : "✗ UYUŞMAZLIK VAR!"));
        System.out.printf("  Sıralı süre  : %.4f ms%n", seq.getLastElapsedMs());
        System.out.printf("  Paralel süre : %.4f ms  (%d thread)%n", par.getLastElapsedMs(), cpus);

        par.shutdown();
    }

    private static void showSpeedup(List<Point> polygon, int n, int cpus) {
        List<Point> points = PolygonUtils.generateRandomPoints(n, 15.0, 99L);

        // 3 tur ısınma
        SequentialSolver seq = new SequentialSolver(polygon);
        for (int i = 0; i < 3; i++) seq.solve(points);

        // 5 tur ölçüm (ortalama)
        long seqTotal = 0;
        for (int i = 0; i < 5; i++) { seq.solve(points); seqTotal += seq.getLastElapsedNs(); }
        double seqMs = seqTotal / 5.0 / 1_000_000.0;

        System.out.printf("%n  Sıralı ortalama (%d nokta): %.4f ms%n", n, seqMs);
        System.out.printf("  %-12s | %-14s | %-10s%n", "Thread Sayısı", "Paralel(ms)", "Speedup");
        System.out.println("  " + "-".repeat(42));

        for (int tc : new int[]{1, 2, 4, cpus}) {
            ParallelSolver par = new ParallelSolver(polygon, tc);
            for (int i = 0; i < 3; i++) par.solve(points);

            long parTotal = 0;
            for (int i = 0; i < 5; i++) { par.solve(points); parTotal += par.getLastElapsedNs(); }
            double parMs  = parTotal / 5.0 / 1_000_000.0;
            double speedup = seqMs / parMs;

            System.out.printf("  %-12d | %-14.4f | %.3fx%n", tc, parMs, speedup);
            par.shutdown();
        }
    }
}
