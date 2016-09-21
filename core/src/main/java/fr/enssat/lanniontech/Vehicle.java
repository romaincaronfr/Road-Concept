package fr.enssat.lanniontech;

public class Vehicle {
    private final double length;
    private double speed; //in m/s
    private double dDone; // in m
    private FrontBackSide frontSide;
    private FrontBackSide backSide;
    private int ID;

    Vehicle(int ID,Lane start,double startPos,double length,double speed){
        this.ID = ID;
        this.length = length;
        this.dDone = 0;
        this.speed = speed;
        this.frontSide = new FrontBackSide(length+startPos,this,start);
        this.backSide = new FrontBackSide(startPos,this,start);
    }

    public void log(){
        System.out.println("ID: "+this.ID);
        System.out.println("distance: "+dDone);
        System.out.println("backSide pos: "+backSide.pos);
        System.out.println("frontSide pos: "+frontSide.pos);
        System.out.println("-----------------------------------------");
    }

    public void move(double time){
        double dDone = this.speed*time;
        this.dDone += dDone;
        backSide.move(dDone);
        frontSide.move(dDone);
    }

}
