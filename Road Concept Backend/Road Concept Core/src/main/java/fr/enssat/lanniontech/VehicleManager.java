package fr.enssat.lanniontech;

import fr.enssat.lanniontech.roadElements.Lane;
import fr.enssat.lanniontech.roadElements.Road;
import fr.enssat.lanniontech.roadElements.RoadSection;
import fr.enssat.lanniontech.vehicleElements.Vehicle;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 4r3 on 01/10/16.
 */
public class VehicleManager {
    private ArrayList<Vehicle> vehicles;
    private ArrayList<Vehicle> activeVehicles;
    private ArrayList<RoadSection> spawnArea;
    private Random gen;

    public VehicleManager(){
        vehicles = new ArrayList<Vehicle>();
        activeVehicles = new ArrayList<Vehicle>();
        spawnArea = new ArrayList<RoadSection>();
        gen = new Random();
    }

    public void addToSpawnArea(Road R){
        for (int i = 0; i < R.size(); i++) {
            addToSpawnArea(R.get(i));
        }
    }

    public void addToSpawnArea(RoadSection RS){
        spawnArea.add(RS);
    }

    public boolean addVehicle(){
        int size = 2+gen.nextInt(12);
        int speed = gen.nextInt(30)+10;

        int startingRoad = gen.nextInt(spawnArea.size()-1);
        Lane startingLane = gen.nextBoolean()?spawnArea.get(startingRoad).getLaneA():spawnArea.get(startingRoad).getLaneB();
        double startingPos = 10+gen.nextInt((int)((startingLane.getLength()-15)*10))/10.0;
        int k = 0;
        System.out.println(startingLane.getLength());
        System.out.println(startingPos);
        while(!startingLane.rangeIsFree(startingPos-15,startingPos+5)){
            startingPos = 10+gen.nextInt((int)((startingLane.getLength()-15)*10))/10.0;
            if(k>10){
                return false;
            }
            k++;
        }
        Vehicle V = new Vehicle(vehicles.size(),startingLane,startingPos,size,speed,0);
        vehicles.add(V);
        activeVehicles.add(V);
        return true;
    }

    public void newStep(double precision,boolean log) {
        for (int j = 0; j < activeVehicles.size(); j++) {
            activeVehicles.get(j).updateAcceleration();
        }

        for (int j = 0; j < activeVehicles.size() ; j++) {
            activeVehicles.get(j).updatePos(precision,log);
        }
    }

    public int getVehiclesNumber() {
        return activeVehicles.size();
    }
}
