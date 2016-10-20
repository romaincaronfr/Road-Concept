package fr.enssat.lanniontech.api.utilities.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.enssat.lanniontech.api.entities.geojson.Coordinates;

import java.io.IOException;

public class LngLatAltSerializer extends JsonSerializer<Coordinates> {

    private static final long POW10[] = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};

    @Override
    public void serialize(Coordinates value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartArray();
        jgen.writeNumber(fastDoubleToString(value.getLongitude(), 9));
        jgen.writeNumber(fastDoubleToString(value.getLatitude(), 9));
        jgen.writeEndArray();
    }

    /**
     * The following must convert double to String in a much more efficient way then Double.toString()
     */
    private static String fastDoubleToString(double val, int precision) {
        StringBuilder sb = new StringBuilder();
        if (val < 0) {
            sb.append('-');
            val = -val;
        }
        long exp = POW10[precision];
        long lval = (long) (val * exp + 0.5);
        sb.append(lval / exp).append('.');
        long fval = lval % exp;
        for (int p = precision - 1; p > 0 && fval < POW10[p] && fval > 0; p--) {
            sb.append('0');
        }
        sb.append(fval);
        int i = sb.length() - 1;
        while (sb.charAt(i) == '0' && sb.charAt(i - 1) != '.') {
            sb.deleteCharAt(i);
            i--;
        }
        return sb.toString();
    }
}
