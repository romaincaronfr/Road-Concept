package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.geojson.Coordinates;

public class FuckIt {

    // x = longitude
    // y = latitude
    public static Coordinates intersect(Coordinates p0, Coordinates p1, Coordinates p2, Coordinates p3) {
        Coordinates intersectionPoint = null;

        double s1_x, s1_y, s2_x, s2_y;
        s1_x = p1.getLongitude() - p0.getLongitude();
        s1_y = p1.getLatitude() - p0.getLatitude();
        s2_x = p3.getLongitude() - p2.getLongitude();
        s2_y = p3.getLatitude() - p2.getLatitude();

        double s,t;
        s = (-s1_y * (p0.getLongitude() - p2.getLongitude()) + s1_x * (p0.getLatitude() - p2.getLatitude())) / (-s2_x * s1_y + s1_x * s2_y);
        t = (s2_x * (p0.getLatitude() - p2.getLatitude()) - s2_y * (p0.getLongitude() - p2.getLongitude())) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            double i_x, i_y;

            i_x = p0.getLongitude() + (t * s1_x);
            i_y = p0.getLatitude() + (t * s1_y);

            intersectionPoint = new Coordinates(i_x,i_y);
        }

        return intersectionPoint;
    }

//    char get_line_intersection(float p0_x, float p0_y, float p1_x, float p1_y, float p2_x, float p2_y, float p3_x, float p3_y, float *i_x, float *i_y) {
//        float s1_x, s1_y, s2_x, s2_y;
//        s1_x = p1_x - p0_x;
//        s1_y = p1_y - p0_y;
//        s2_x = p3_x - p2_x;
//        s2_y = p3_y - p2_y;
//
//        float s, t;
//        s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
//        t = (s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);
//
//        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
//            // Collision detected
//            if (i_x != NULL)
//            *i_x = p0_x + (t * s1_x);
//            if (i_y != NULL)
//            *i_y = p0_y + (t * s1_y);
//            return 1;
//        }
//
//        return 0; // No collision
//    }
}

