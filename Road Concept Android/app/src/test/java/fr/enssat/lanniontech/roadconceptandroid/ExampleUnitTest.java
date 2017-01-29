package fr.enssat.lanniontech.roadconceptandroid;

import org.junit.Test;

import fr.enssat.lanniontech.roadconceptandroid.Utilities.ImageFactory;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_remove_string() throws Exception {
        assertEquals("ijkl", ImageFactory.removeBase64inString("img/png.base64,ijkl"));
    }
}