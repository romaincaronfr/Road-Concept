package fr.enssat.lanniontech;

/**
 * Created by 4r3 on 25/09/16.
 */
public class PosFunction {
    private double alat;
    private double blat;
    private double clat;

    private double alon;
    private double blon;
    private double clon;



    PosFunction(Position P1,Position P2,double length){
        //calculate the parrameters
        blat = P1.lat;
        alat = (P2.lat - P1.lat)/length;
        blon = P1.lon;
        alon = (P2.lon - P1.lon)/length;

        clat = -alat;
        clon = alon;

    }

    public Position get(double pos){
        return get(pos,0);
    }

    public Position get(double pos, double wpos){
        double lat = alat*pos+blat+clat*wpos;
        double lon = alon*pos+blon+clon*wpos;
        return new Position(lat,lon);
    }


}
