package fr.enssat.lanniontech.api.utilities;

import com.rits.cloning.Cloner;

public class GlobalUtils {

    private GlobalUtils() {
        // prevent instantiation
    }

    public static final Cloner CLONER = new Cloner();

}
