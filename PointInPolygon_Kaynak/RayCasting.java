import java.util.List;

public class RayCasting {

    private RayCasting() { /* Yardımcı sınıf, örneklenemez */ }

    /**
     * @param point   test edilecek nokta
     * @param polygon sıralı köşe listesi (son köşe ile ilk köşe otomatik bağlanır)
     * @return true → içeride, false → dışarıda
     */
    public static boolean isInside(Point point, List<Point> polygon) {
        int n = polygon.size();
        if (n < 3) return false;   // Geçersiz poligon

        boolean inside = false;
        double px = point.x;
        double py = point.y;

        // Her kenarı kontrol et: (polygon[i], polygon[j])
        int j = n - 1;
        for (int i = 0; i < n; i++) {
            double xi = polygon.get(i).x,  yi = polygon.get(i).y;
            double xj = polygon.get(j).x,  yj = polygon.get(j).y;

            boolean yCondition = (yi > py) != (yj > py);
            if (yCondition) {
                double xIntersect = (xj - xi) * (py - yi) / (yj - yi) + xi;
                if (px < xIntersect) {
                    inside = !inside;   // Her kesişimde toggle
                }
            }
            j = i;
        }
        return inside;
    }
}
