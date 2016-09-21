package fr.enssat.lanniontech;


import java.util.ArrayList;

public class Vehicle {
    private final double length;
    private double speed; //in m/s
    private double dDone; // in m
    private ArrayList<Cell> cells;
    private double dCarr;
    private double dCavt;

    Vehicle(Cell start,double length,double speed){
        this.length = length;
        this.dDone = 0;
        this.speed = speed;
        cells = new ArrayList<Cell>();
        dCarr=0;
        cells.add(start);
        start.setOccupied(this);
        length -= 0.5;
        Cell nextCell;
        while(length>0.5){
            nextCell = cells.get(0).getNextCell();
            nextCell.setOccupied(this);
            cells.add(nextCell);
            length -= 0.5;
        }
        nextCell = cells.get(0).getNextCell();
        nextCell.setOccupied(this);
        cells.add(nextCell);
        dCavt = length;
    }

    public void log(){
        System.out.println("distance: "+dDone);
        System.out.println("car pos in last cell: "+dCarr);
        System.out.println("number of cell occupied: "+cells.size());
        System.out.println("car pos in first cell: "+dCavt);
        System.out.println("-----------------------------------------");
    }

    public void move(double time){
        double dDone = this.speed*time;
        dCavt += dDone;
        dCarr += dDone;
        this.dDone += dDone;

        Cell nextCell;
        while(dCavt>0.5){
            nextCell = cells.get(0).getNextCell();
            nextCell.setOccupied(this);
            cells.add(nextCell);
            dCavt -= 0.5;
        }

        Cell lastCell;
        while(dCarr>=0.5){
            lastCell = cells.get(cells.size()-1);
            lastCell.setOccupied(null);
            cells.remove(lastCell);
            dCarr -= 0.5;
        }
    }

}
