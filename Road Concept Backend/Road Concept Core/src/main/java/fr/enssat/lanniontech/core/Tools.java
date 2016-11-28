package fr.enssat.lanniontech.core;

public class Tools {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static double getOrientedAngle(double mainX,double mainY,double X,double Y){
        double res;
        double dotProduct = mainX*X + mainY*Y;
        double mainNorm = Math.sqrt(mainX*mainX+mainY*mainY);
        double norm = Math.sqrt(X*X+Y*Y);

        res = Math.acos(dotProduct/(mainNorm*norm));

        if(mainX*Y - mainY*X < 0){
            res = res*-1;
        }

        return res;
    }

    public static double mpsToKph(double mps) {
        return mps * 3.6;
    }

    public static double kphToMph(double kph) {
        return kph / 3.6;
    }
}
