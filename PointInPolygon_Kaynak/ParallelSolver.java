import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelSolver {

    private final List<Point>  polygon;
    private final int          threadCount;
    private final ExecutorService executor;
    private long lastElapsedNs;

    /**
     * @param polygon     test edilecek poligonun köşe listesi
     * @param threadCount kullanılacak thread sayısı
     *                    (önerilen: Runtime.getRuntime().availableProcessors())
     */
    public ParallelSolver(List<Point> polygon, int threadCount) {
        this.polygon     = polygon;
        this.threadCount = threadCount;
        // ForkJoinPool: work-stealing destekli; ince taneli görevlerde verimli.
        this.executor    = new ForkJoinPool(threadCount);
    }

    /**
     *
     * @param points test edilecek noktalar
     * @return results[i] = true ise points.get(i) poligon içindedir
     * @throws RuntimeException iş parçacığı kesmesi durumunda
     */
    public boolean[] solve(List<Point> points) {
        int n       = points.size();
        boolean[] results = new boolean[n];

        int chunkSize = Math.max(1, (int) Math.ceil((double) n / threadCount));
        List<Callable<Void>> tasks = new ArrayList<>(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int start = t * chunkSize;
            final int end   = Math.min(start + chunkSize, n);
            if (start >= n) break;   // Son thread'e iş kalmadıysa atla

            tasks.add(() -> {
                for (int i = start; i < end; i++) {
                    results[i] = RayCasting.isInside(points.get(i), polygon);
                }
                return null;
            });
        }
        long startTime = System.nanoTime();
        try {
            List<Future<Void>> futures = executor.invokeAll(tasks);
            // Olası exception'ları yüzey çıkar
            for (Future<Void> f : futures) {
                f.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Paralel işlem kesildi.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Thread içinde hata oluştu.", e.getCause());
        }
        lastElapsedNs = System.nanoTime() - startTime;

        return results;
    }

    public long getLastElapsedNs() {
        return lastElapsedNs;
    }

    public double getLastElapsedMs() {
        return lastElapsedNs / 1_000_000.0;
    }

    public void shutdown() {
        executor.shutdown();
    }

    public int getThreadCount() {
        return threadCount;
    }
}
