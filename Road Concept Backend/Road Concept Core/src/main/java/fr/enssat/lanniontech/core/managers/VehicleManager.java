package fr.enssat.lanniontech.core.managers;

import fr.enssat.lanniontech.core.pathFinding.Path;
import fr.enssat.lanniontech.core.pathFinding.PathFinder;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.roadElements.Road;
import fr.enssat.lanniontech.core.roadElements.RoadSection;
import fr.enssat.lanniontech.core.vehicleElements.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class VehicleManager {
    private List<Vehicle> vehicles;
    private List<Vehicle> activeVehicles;
    private Road livingArea;
    private Road workingArea;
    private Random gen;
    private HistoryManager historyManager;
    private RoadManager roadManager;
    private PathFinder pathFinder;

    public VehicleManager(HistoryManager history, RoadManager roadManager) {
        historyManager = history;
        vehicles = new ArrayList<>();
        activeVehicles = new ArrayList<>();
        this.roadManager = roadManager;
        gen = new Random();
        pathFinder = new PathFinder(roadManager);
    }

    public void setLivingArea(UUID id){
        livingArea = roadManager.getRoad(id);
    }

    public void setWorkingArea(UUID id){
        workingArea = roadManager.getRoad(id);
    }

    public void createTrafficGenerator(int goTimestamp,int returnTimestamp,int vehicles,int ratio){
        //todo
    }

    public boolean addVehicle() {
        /*
        Path myPath = pathFinder.getRandomPath(startingLane.getInsertTrajectory(), 100);
        Vehicle V = Vehicle.createCar(vehicles.size(), startingLane, startingPos, historyManager, myPath);
        vehicles.add(V);
        activeVehicles.add(V);
        V.logPosition(0);*/
        return true;
    }

    public void newStep(double precision, boolean log, int timestamp) {
        for (Vehicle activeVehicle : activeVehicles) {
            activeVehicle.updateAcceleration();
        }

        for (Vehicle activeVehicle : activeVehicles) {
            activeVehicle.updatePos(precision);
            if (log) {
                activeVehicle.logPosition(timestamp);
            }
        }

        int i = 0;
        while (i < activeVehicles.size()) {
            if (activeVehicles.get(i).isArrived()) {
                activeVehicles.remove(i);
            } else {
                i++;
            }
        }

    }

    public int getVehiclesNumber() {
        return activeVehicles.size();
    }
}
