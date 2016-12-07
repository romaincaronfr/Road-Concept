package fr.enssat.lanniontech.core.managers;

import fr.enssat.lanniontech.core.generators.DiracGenerator;
import fr.enssat.lanniontech.core.generators.Generator;
import fr.enssat.lanniontech.core.generators.VehicleKernel;
import fr.enssat.lanniontech.core.pathFinding.Path;
import fr.enssat.lanniontech.core.pathFinding.PathFinder;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.roadElements.Road;
import fr.enssat.lanniontech.core.roadElements.RoadSection;
import fr.enssat.lanniontech.core.vehicleElements.Vehicle;
import fr.enssat.lanniontech.core.vehicleElements.VehicleType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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
    private List<Generator> generators;
    private List<List<VehicleKernel>> vehiclesBuffers;

    public VehicleManager(HistoryManager history, RoadManager roadManager) {
        historyManager = history;
        vehicles = new ArrayList<>();
        activeVehicles = new ArrayList<>();
        generators = new ArrayList<>();
        vehiclesBuffers = new ArrayList<>();
        this.roadManager = roadManager;
        gen = new Random();
        pathFinder = new PathFinder(roadManager);

        //todo replace by a better solution (hash map)
        vehiclesBuffers.add(new LinkedList<>());//add home buffer
        vehiclesBuffers.add(new LinkedList<>());//add work buffer
    }

    public void setLivingArea(UUID id) {
        livingArea = roadManager.getRoad(id);
    }

    public void setWorkingArea(UUID id) {
        workingArea = roadManager.getRoad(id);
    }

    public void createTrafficGenerator(int goTimestamp, int returnTimestamp, int vehicles, int carPercentage) {
        generators.add(new DiracGenerator(goTimestamp, vehicles, carPercentage));       //add the home generator
        generators.add(new DiracGenerator(returnTimestamp, vehicles, carPercentage));   //add the work generator
    }

    public void updateBuffers(long timestamp) {
        for (int i = 0; i < generators.size(); i++) {
            vehiclesBuffers.get(i).addAll(generators.get(i).addVehicles(timestamp));
        }
    }

    private void insertBuffers() {
        Iterator<VehicleKernel> iterator = vehiclesBuffers.get(0).iterator();
        while (iterator.hasNext()) {
            VehicleKernel kernel = iterator.next();
            if (addVehicle(kernel, livingArea, workingArea)) {
                iterator.remove();
            } else {
                break;
            }
        }

        iterator = vehiclesBuffers.get(1).iterator();

        while (iterator.hasNext()) {
            VehicleKernel kernel = iterator.next();
            if (addVehicle(kernel, workingArea, livingArea)) {
                iterator.remove();
            } else {
                break;
            }
        }
    }

    private boolean addVehicle(VehicleKernel kernel, Road start, Road stop) {
        RoadSection Rs = start.get(gen.nextInt(start.size()));
        Lane startingLane;
        if (gen.nextBoolean()) {
            startingLane = Rs.getLaneAB();
        } else {
            startingLane = Rs.getLaneBA();
        }

        double startingPos = gen.nextInt((int) startingLane.getLength());

        if (!startingLane.getInsertTrajectory().rangeIsFree(startingPos - 3, startingPos + 3)) {
            return false;
        }

        //Path myPath = pathFinder.getRandomPath(startingLane.getInsertTrajectory(), 10);
        Path myPath = pathFinder.getPathTo(startingLane.getInsertTrajectory(), stop.getId(), false);
        Vehicle V;
        if (kernel.getType() == VehicleType.CAR) {
            V = Vehicle.createCar(vehicles.size(), startingLane, startingPos, historyManager, myPath);
        } else {
            V = Vehicle.createTruck(vehicles.size(), startingLane, startingPos, historyManager, myPath);
        }
        vehicles.add(V);
        activeVehicles.add(V);
        return true;
    }

    public void newStep(double precision, boolean log, int timestamp) {

        insertBuffers();

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
