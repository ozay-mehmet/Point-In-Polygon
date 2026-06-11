import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PolygonUtils {

    /**
     * @param cx     merkez x koordinatı
     * @param cy     merkez y koordinatı
     * @param r      çemberin yarıçapı
     * @param sides  kenar sayısı (en az 3)
     * @return köşe noktalarının listesi
     */
    public static List<Point> createConvexPolygon(double cx, double cy,
                                                   double r, int sides) {
        if (sides < 3) throw new IllegalArgumentException("En az 3 kenar gerekli.");
        List<Point> polygon = new ArrayList<>();
        for (int i = 0; i < sides; i++) {
            double angle = 2 * Math.PI * i / sides - Math.PI / 2;
            polygon.add(new Point(cx + r * Math.cos(angle),
                                  cy + r * Math.sin(angle)));
        }
        return polygon;
    }

    /**
     * @return concave poligon köşeleri
     */
    public static List<Point> createConcavePolygon() {
        // "Yıldız" şeklinde içbükey çokgen — 12 köşe
        List<Point> polygon = new ArrayList<>();
        int points = 6;
        double outerR = 10.0;
        double innerR = 4.5;
        for (int i = 0; i < points * 2; i++) {
            double angle = Math.PI * i / points - Math.PI / 2;
            double r = (i % 2 == 0) ? outerR : innerR;
            polygon.add(new Point(r * Math.cos(angle), r * Math.sin(angle)));
        }
        return polygon;
    }

    /**
     * @param n      nokta sayısı
     * @param range  koordinat aralığı
     * @param seed   tekrarlanabilirlik için tohum
     * @return rastgele noktaların listesi
     */
    public static List<Point> generateRandomPoints(int n, double range, long seed) {
        List<Point> points = new ArrayList<>(n);
        Random rnd = new Random(seed);
        for (int i = 0; i < n; i++) {
            double x = (rnd.nextDouble() * 2 - 1) * range;
            double y = (rnd.nextDouble() * 2 - 1) * range;
            points.add(new Point(x, y));
        }
        return points;
    }

    /**
     * @param polygon köşe listesi
     * @param label   başlık etiketi
     */
    public static void printPolygon(List<Point> polygon, String label) {
        System.out.println("\n=== " + label + " (" + polygon.size() + " köşe) ===");
        for (int i = 0; i < polygon.size(); i++) {
            System.out.printf("  Köşe %2d: %s%n", i, polygon.get(i));
        }
    }
}
