package fr.enssat.lanniontech.core.vehicleElements;

import fr.enssat.lanniontech.core.Tools;

public class VehicleAI {
    private final double a;           //max acceleration
    private final double b;           //deceleration
    private final double T;           //gap time between two car
    private final double lambda = 4;  //function coefficient
    private final double s0 = 2;      //minimum distance between two cars

    private double A;           //acceleration
    private double Va = Tools.kphToMph(90);      //speed in m/s

    public VehicleAI(double a, double b, double t) {
        this.a = a;
        this.b = b;
        T = t;
    }

    public void updateAcceleration(double distanceToNext, double speedOfNext, double v0) {
        double Sprime = s0 + Va * T + (Va * (Va - speedOfNext)) / (2 * Math.sqrt(a * b));
        A = a * (1 - Math.pow(Va / v0, lambda) - Math.pow(Sprime / distanceToNext, 2));
        if (Double.isNaN(A)) {
            System.err.println("overflow");
        }
        A=0;
    }

    public double getDistanceDone(double time){
        double dDone = Va * time;
        Va += A * time;
        return dDone;
    }

    public double getSpeed() {
        return Va;
    }

    public double getFreeDistance(){
        return 100;
        //todo compute real free distance
    }
}
