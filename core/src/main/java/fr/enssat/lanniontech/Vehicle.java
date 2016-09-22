package fr.enssat.lanniontech;

public class Vehicle {
    private final double length;
    private double distanceDone;// in m
    private FrontBackSide frontSide;
    private FrontBackSide backSide;
    private int ID;

    private double Va = 0;      //speed in m/s
    private double A;           //acceleration
    private double a = 2;       //max acceleration
    private double b = 1;       //deceleration
    private double T = 1;       //gap time between two car
    private double lambda = 4;
    private double v0;          //desired speed
    private double s0 = 2;      //minimum distance between two cars


    Vehicle(int ID,Lane start,double startPos,double length,double speed){
        this.ID = ID;
        this.length = length;
        this.distanceDone = 0;
        this.v0 = speed;
        this.frontSide = new FrontBackSide(length+startPos,this,start);
        this.backSide = new FrontBackSide(startPos,this,start);
    }

    public void log(){
        System.out.println("ID: " + this.ID);
        System.out.println("distance: " + distanceDone);
        System.out.println("speed: " + Va);
        //System.out.println("backSide pos: "+backSide.pos);
        //System.out.println("frontSide pos: "+frontSide.pos);
        System.out.println("Acceleration: " + A);
        System.out.println("distance to next car: "+distanceToNextCar());
        System.out.println("-----------------------------------------");
    }

    public void updateSpeed(){
        double Sa = this.distanceToNextCar();
        double Sprime = s0 +Va*T+(Va*(Va-nextCarSpeed()))/(2*Math.sqrt(a*b));
        A=a*(1-Math.pow(Va/v0,lambda)-Math.pow(Sprime/Sa,2));
    }

    private double distanceToNextCar() {
        return frontSide.getdistanceToNextCar();
    }

    private double nextCarSpeed(){
        return frontSide.getNextCarSpeed();
    }

    public void updatePos(double time){
        double dDone = Va*time;
        Va+=A*time;
        this.distanceDone += dDone;
        backSide.move(dDone);
        frontSide.move(dDone);
    }

    public double getSpeed(){
        return Va;
    }
}
