package fr.enssat.lanniontech.core.vehicleElements;

public class VehicleAI {
    private final double a;           //max acceleration
    private final double b;           //deceleration
    private final double T;           //gap time between two car
    private final double lambda = 4;  //function coefficient
    private final double s0 = 2;      //minimum distance between two cars

    private double A;           //acceleration
    private double Va = 0;      //speed in m/s

    public VehicleAI(double a, double b, double t) {
        this.a = a;
        this.b = b;
        T = t;
    }

    /**
     * @param distanceToNext
     *         distance to next car in m
     * @param speedOfNext
     *         speed of the next car in m/s
     * @param v0
     *         desired speed in m/s
     */
    public void updateAcceleration(double distanceToNext, double speedOfNext, double v0) {
        double deltaSpeed = Va - speedOfNext;
        double freeRoadCoeff = Math.pow(Va / v0, 4);
        double timeGap = Va * T;
        double breakGap = Va * deltaSpeed / (2 * Math.sqrt(a * b));
        double safeDistance = s0 + timeGap + breakGap;
        double busyRoadCoeff = Math.pow(safeDistance / distanceToNext, 2);
        double coeff = 1 - freeRoadCoeff - busyRoadCoeff;
        A = a * coeff;/*
        double Sprime = s0 + Va * T + (Va * (Va - speedOfNext)) / (2 * Math.sqrt(a * b));
        A = a * (1 - Math.pow(Va / v0, lambda) - Math.pow(Sprime / distanceToNext, 2));
        if(A > a || A < -b){
            System.out.print("bug");
        }/**/
    }

    public double getDistanceDone(double time) {
        double dDone = Va * time;
        Va += A * time;
        if(Va < 0){
            Va = 0;
        }
        return dDone;
    }

    public double getSpeed() {
        return Va;
    }

    public double getFreeDistance(double maxSpeed) {
        return maxSpeed * T * 5;
    }
}
