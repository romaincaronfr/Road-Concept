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

public class VehicleManager {
    private List<Vehicle> vehicles;
    private List<Vehicle> activeVehicles;
    private List<RoadSection> spawnArea;
    private Random gen;
    private HistoryManager historyManager;
    private PathFinder pathFinder;

    public VehicleManager(HistoryManager history, RoadManager roadManager) {
        historyManager = history;
        vehicles = new ArrayList<>();
        activeVehicles = new ArrayList<>();
        spawnArea = new ArrayList<>();
        gen = new Random();
        pathFinder = new PathFinder(roadManager);
    }

    public void addToSpawnArea(Road R) {
        for (int i = 0; i < R.size(); i++) {
            addToSpawnArea(R.get(i));
        }
    }

    public void addToSpawnArea(RoadSection RS) {
        spawnArea.add(RS);
    }

    public boolean addVehicle() {
        int size = 2 + gen.nextInt(12);
        int speed = gen.nextInt(30) + 10;


        int startingRoad = 0;
        if (spawnArea.size() > 1) {
            startingRoad = gen.nextInt(spawnArea.size() - 1);
        }

        Lane startingLane = gen.nextBoolean() ? spawnArea.get(startingRoad).getLaneAB() : spawnArea.get(startingRoad).getLaneBA();
        double startingPos = 4;
        int k = 0;
        System.out.println(startingLane.getLength());
        System.out.println(startingPos);
        while (!startingLane.getInsertTrajectory().rangeIsFree(startingPos - 15, startingPos + 5)) {
            startingPos = 10 + gen.nextInt((int) ((startingLane.getLength() - 15) * 10)) / 10.0;
            if (k > 10) {
                return false;
            }
            k++;
        }

        Path myPath = pathFinder.getRandomPath(startingLane.getInsertTrajectory(), 10);
        Vehicle V = new Vehicle(vehicles.size(), startingLane, startingPos, size,
                speed, historyManager, myPath);
        vehicles.add(V);
        activeVehicles.add(V);
        V.logPosition(0);
        return true;
    }

    public void newStep(double precision, boolean log, long timestamp) {
        for (Vehicle activeVehicle : activeVehicles) {
            activeVehicle.updateAcceleration();
        }

        for (Vehicle activeVehicle : activeVehicles) {
            activeVehicle.updatePos(precision);
            activeVehicle.logPosition(timestamp);
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
