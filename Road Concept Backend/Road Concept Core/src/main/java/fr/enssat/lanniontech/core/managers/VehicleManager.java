package fr.enssat.lanniontech.core.managers;

import fr.enssat.lanniontech.core.exceptions.DestinationUnreachableException;
import fr.enssat.lanniontech.core.generators.*;
import fr.enssat.lanniontech.core.pathFinding.Path;
import fr.enssat.lanniontech.core.pathFinding.PathFinder;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.roadElements.roadSections.DualWayRoadSection;
import fr.enssat.lanniontech.core.roadElements.roadSections.OneWayRoadSection;
import fr.enssat.lanniontech.core.roadElements.roadSections.RoadSection;
import fr.enssat.lanniontech.core.roadElements.roads.Road;
import fr.enssat.lanniontech.core.vehicleElements.Vehicle;
import fr.enssat.lanniontech.core.vehicleElements.VehicleStats;
import fr.enssat.lanniontech.core.vehicleElements.VehicleType;
import sun.security.krb5.internal.PAData;

import java.util.*;

public class VehicleManager {
    private List<Vehicle> vehicles;
    private List<Vehicle> activeVehicles;
    private Random gen;
    private HistoryManager historyManager;
    private RoadManager roadManager;
    private PathFinder pathFinder;
    private Map<Generator,VehicleBuffer> vehiclesBuffers;

    public VehicleManager(HistoryManager history, RoadManager roadManager) {
        historyManager = history;
        vehicles = new ArrayList<>();
        activeVehicles = new ArrayList<>();
        vehiclesBuffers = new HashMap<>();
        this.roadManager = roadManager;
        gen = new Random();
        pathFinder = new PathFinder(roadManager);

    }

    public void createTrafficGenerator(int goTimestamp, int returnTimestamp,
                                       int vehicles, int carPercentage,
                                       UUID A1, UUID A2) {
        Road R1 = roadManager.getRoad(A1);
        Road R2 = roadManager.getRoad(A2);
        Generator G = new UniformGenerator(goTimestamp, vehicles, carPercentage,3600);
        VehicleBuffer B = new VehicleBuffer(R1,R2);
        vehiclesBuffers.put(G,B);

        G = new UniformGenerator(returnTimestamp, vehicles, carPercentage,3600);
        B = new VehicleBuffer(R2,R1);
        vehiclesBuffers.put(G,B);
    }

    public void createRandomTrafficGenerator(int start,int stop,int vehicles,int carPercentage){
        Generator G = new UniformGenerator(start,vehicles,carPercentage,stop-start);
        List<Road> possibleSpawns = roadManager.getRoads();
        VehicleBuffer B = new VehicleBuffer(possibleSpawns,possibleSpawns);
        vehiclesBuffers.put(G,B);
    }



    public void updateBuffers(long timestamp) {
        for (Generator G : vehiclesBuffers.keySet()){
            vehiclesBuffers.get(G).addKernels(G.addVehicles(timestamp));
        }
    }

    private void insertBuffers() {
        int attempts;
        boolean res;
        for(VehicleBuffer B : vehiclesBuffers.values()){
            attempts = 0;
            while(!B.isEmpty() && attempts < 5){
                res = addVehicle(B.getKernel(),B.getSource(),B.getDestination());
                if(res){
                    B.removeKernel();
                }else {
                    attempts ++;
                }
            }
        }
    }

    private boolean addVehicle(VehicleKernel kernel, Road start, Road stop) {
        RoadSection Rs = start.get(gen.nextInt(start.size()));
        Lane startingLane;
        if (Rs instanceof DualWayRoadSection) {
            if (gen.nextBoolean()) {
                startingLane = ((DualWayRoadSection) Rs).getLaneAB();
            } else {
                startingLane = ((DualWayRoadSection) Rs).getLaneBA();
            }
        } else {
            startingLane = ((OneWayRoadSection) Rs).getLane();
        }

        double startingPos = gen.nextInt((int) startingLane.getLength());

        if (!startingLane.getInsertTrajectory().rangeIsFree(startingPos, 20, 40)) {
            return false;
        }
        Path myPath;
        try{
            myPath = pathFinder.getPathTo(startingLane.getInsertTrajectory(), stop.getId(), false);
        }catch (DestinationUnreachableException e){
            e.printStackTrace();
            return false;
        }
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
                activeVehicles.get(i).removeVehicle();
                activeVehicles.remove(i);
            } else {
                i++;
            }
        }

    }

    public int getVehiclesNumber() {
        return activeVehicles.size();
    }

    public List<VehicleStats> getStatistics() {
        List<VehicleStats> stats = new ArrayList<>();
        for (Vehicle V : vehicles) {
            stats.add(V.getStats());
        }
        return stats;
    }
}
