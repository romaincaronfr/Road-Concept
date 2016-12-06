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
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static boolean intersect(Coordinates p0, Coordinates p1, Coordinates p2) {
        double aX = p0.getLongitude();
        double aY = p0.getLatitude();
        double bX = p1.getLongitude();
        double bY = p1.getLatitude();
        double cX = p2.getLongitude();
        double cY = p2.getLatitude();

        double distanceAB = Math.sqrt(Math.pow(aX - bX, 2) + Math.pow(aY - bY, 2));
        double distanceAC = Math.sqrt(Math.pow(aX - cX, 2) + Math.pow(aY - cY, 2));
        double distanceCB = Math.sqrt(Math.pow(cX - bX, 2) + Math.pow(cY - bY, 2));
        double distancePoint = distanceAC + distanceCB;

        return distancePoint > distanceAB - 0.0003 && distancePoint < distanceAB + 0.0003;
    }
}
