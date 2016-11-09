package fr.enssat.lanniontech.tests;

import fr.enssat.lanniontech.api.utilities.MathsUtils;
import org.junit.Test;

public class RoundTest {

    @Test
    public void testRoundDouble() {
        double latitude = -3.455965187041778;
        double result = MathsUtils.round(latitude,7);
        System.out.println(result);
    }
}
