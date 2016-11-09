package fr.enssat.lanniontech.api.utilities;

import fr.enssat.lanniontech.api.entities.geojson.Coordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MathsUtils.class);

    public static double roundGPS(double value) {
        return round(value, 7);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static boolean intersect(Coordinates p0, Coordinates p1, Coordinates p2, Coordinates p3) {
        double s1_x = p1.getLongitude() - p0.getLongitude();
        double s1_y = p1.getLatitude() - p0.getLatitude();
        double s2_x = p3.getLongitude() - p2.getLongitude();
        double s2_y = p3.getLatitude() - p2.getLatitude();

        double s = (-s1_y * (p0.getLongitude() - p2.getLongitude()) + s1_x * (p0.getLatitude() - p2.getLatitude())) / (-s2_x * s1_y + s1_x * s2_y);
        double t = (s2_x * (p0.getLatitude() - p2.getLatitude()) - s2_y * (p0.getLongitude() - p2.getLongitude())) / (-s2_x * s1_y + s1_x * s2_y);

        //if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
        LOGGER.debug("s = " + s + " | t = " + t);
        if (s >= -0.1 && s <= 0.9 && t >= -0.1 && t <= 1.1) {
            double i_x = p0.getLongitude() + (t * s1_x);
            double i_y = p0.getLatitude() + (t * s1_y);

            return true;
        }
        return false;
        /*Point2D point0 = new Point2D.Double(p0.getLongitude(),p0.getLatitude());
        Point2D point1 = new Point2D.Double(p1.getLongitude(),p1.getLatitude());
        Point2D point2 = new Point2D.Double(p2.getLongitude(),p2.getLatitude());

        LOGGER.debug("@@@ DISTANCE = "+new Line2D.Double(point0, point1).ptLineDist(point2));

        //LOGGER.debug("@@@ Distance = "+new Line2D.Double(point0, point1).ptLineDist(point2));
        return (new Line2D.Double(point0, point1).ptLineDist(point2) < Math.pow(10,-6));*/
    }
}
