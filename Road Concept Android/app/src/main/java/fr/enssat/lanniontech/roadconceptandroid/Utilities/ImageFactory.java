package fr.enssat.lanniontech.roadconceptandroid.Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by Romain on 11/01/2017.
 */

public class ImageFactory {

    public static Bitmap getBitmapWithBase64 (String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
