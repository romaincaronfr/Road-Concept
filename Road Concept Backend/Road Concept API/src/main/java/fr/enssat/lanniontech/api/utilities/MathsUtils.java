package fr.enssat.lanniontech.api.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathsUtils {

    public static double roundGPS(double value) {
        return round(value, 7);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
