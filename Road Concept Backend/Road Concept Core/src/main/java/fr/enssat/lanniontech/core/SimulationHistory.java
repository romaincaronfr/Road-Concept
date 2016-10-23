package fr.enssat.lanniontech.core;

import fr.enssat.lanniontech.core.positioning.SpaceTimePosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class SimulationHistory {
    private HashMap<Integer,LinkedHashMap<Long,SpaceTimePosition>> vehiclePositionsFromId;
    //structure: <VehicleId,<timestamp,Position>>
    private LinkedHashMap<Long,TreeMap<Double,TreeMap<Double,Integer>>> vehicleIdFromPosition;
    //structure: <timestamp,<Longitude,<Latitude,VehicleId>>>


    public SimulationHistory() {
        vehiclePositionsFromId = new HashMap<>();
        vehicleIdFromPosition = new LinkedHashMap<>();
    }

    public void AddPosition(SpaceTimePosition P) {
        if (!vehiclePositionsFromId.containsKey(P.getId())) {
            vehiclePositionsFromId.put(P.getId(), new LinkedHashMap<>());
        }
        vehiclePositionsFromId.get(P.getId()).put(P.getTime(), P);

        vehicleIdFromPosition.putIfAbsent(P.getTime(),new TreeMap<>());
        vehicleIdFromPosition.get(P.getTime()).putIfAbsent(P.getLon(),new TreeMap<>());
        vehicleIdFromPosition.get(P.getTime()).get(P.getLon()).putIfAbsent(P.getLat(),P.getId());

    }

    public ArrayList<SpaceTimePosition> getVehiclePosition(int vehicleId){
        ArrayList<SpaceTimePosition> res = new ArrayList<>();

        res.addAll(vehiclePositionsFromId.get(vehicleId).values());

        return res;
    }

    public ArrayList<SpaceTimePosition> getVehiclesIn(double minLongitude,double maxLongitude,double minLatitude,double maxLatitude,long timestamp){
        ArrayList<SpaceTimePosition> vehiclesIn =  new ArrayList<>();
        for (double longitude : vehicleIdFromPosition.get(timestamp).keySet()){
            if(longitude > maxLongitude){
                break;
            }else if (longitude > minLongitude){
                for(double latitude:vehicleIdFromPosition.get(timestamp).get(longitude).keySet()){
                    if(latitude > maxLatitude) {
                        break;
                    }else if (latitude > minLatitude){
                        vehiclesIn.add(vehiclePositionsFromId.get(vehicleIdFromPosition.get(timestamp).get(longitude).get(latitude)).get(timestamp));
                    }
                }
            }
        }
        return vehiclesIn;
    }

}
