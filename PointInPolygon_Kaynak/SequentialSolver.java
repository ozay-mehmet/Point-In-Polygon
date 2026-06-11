import java.util.List;

public class SequentialSolver {

    private final List<Point> polygon;
    private long lastElapsedNs;

    /**
     * @param polygon test edilecek poligonun köşe listesi
     */
    public SequentialSolver(List<Point> polygon) {
        this.polygon = polygon;
    }

    /**
     * @param points test edilecek noktalar
     * @return results[i] = true ise points[i] içeridedir
     */
    public boolean[] solve(List<Point> points) {
        int n = points.size();
        boolean[] results = new boolean[n];

        long start = System.nanoTime();

        for (int i = 0; i < n; i++) {
            results[i] = RayCasting.isInside(points.get(i), polygon);
        }

        lastElapsedNs = System.nanoTime() - start;
        return results;
    }

    public long getLastElapsedNs() {
        return lastElapsedNs;
    }

    public double getLastElapsedMs() {
        return lastElapsedNs / 1_000_000.0;
    }
}
