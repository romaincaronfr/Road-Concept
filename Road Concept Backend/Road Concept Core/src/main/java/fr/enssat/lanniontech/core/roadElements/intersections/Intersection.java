package fr.enssat.lanniontech.core.roadElements.intersections;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.RoadSection;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;

import java.util.*;

public class Intersection {

    private Position P;
    private Map<UUID, RoadSection> roadSections;
    private Map<UUID, Map<UUID, SimpleTrajectory>> trajectories;
    //structure is <source ,<destination, AdvancedTrajectory>>

    public Intersection(Position P) {
        this.P = P;
        roadSections = new HashMap<>();
        trajectories = new HashMap<>();
    }

    private void addTrajectories(UUID id) {

    }

    public boolean addRoadSection(RoadSection Rs) {
        if (Rs.getA() != P && Rs.getB() != P) {
            return false;
        }
        roadSections.put(Rs.getMyRoad().getId(), Rs);
        addTrajectories(Rs.getMyRoad().getId());
        return true;
    }

    public int getTrajectoriesSize() {
        int s = 0;
        for (UUID uuid : trajectories.keySet()) {
            s += trajectories.get(uuid).size();
        }
        return s;
    }

    public int getRoadSectionsSize() {
        return roadSections.size();
    }

    public boolean removeRoadSection(UUID id) {
        if (roadSections.containsKey(id)) {
            for (UUID uuid : trajectories.keySet()) {
                trajectories.get(uuid).remove(id);
            }
            trajectories.remove(id);
            roadSections.remove(id);
        } else {
            return false;
        }
        return true;
    }

    public List<SimpleTrajectory> getTrajectories() {
        List<SimpleTrajectory> trajectories = new ArrayList<>();
        for (Map<UUID,SimpleTrajectory> map : this.trajectories.values()){
            trajectories.addAll(map.values());
        }
        return trajectories;
    }

    public List<SimpleTrajectory> getTrajectoriesFrom(UUID source) {
        List<SimpleTrajectory> trajectories = new ArrayList<>();
        trajectories.addAll(this.trajectories.get(source).values());
        return trajectories;
    }

    public Position getP() {
        return P;
    }
}
