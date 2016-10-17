package fr.enssat.lanniontech.core.positioning;

public class SpaceTimePosition extends Position {

    long time;
    double angle;

    SpaceTimePosition(double lon,double lat,long time){
        this(lon,lat,time,0);
    }

    SpaceTimePosition(double lon,double lat,long time, double angle){
        super(lon,lat);

        this.time = time;
        this.angle = angle;
    }

    public static SpaceTimePosition getMean(Position A, Position B, long time){
        double lon = (A.lon+B.lon)/2;
        double lat = (A.lat+B.lat)/2;
        //todo compute angle from A and B
        return new SpaceTimePosition(lon,lat,time);
    }
}
