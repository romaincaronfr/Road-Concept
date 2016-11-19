package fr.enssat.lanniontech.core.roadElements.intersections;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.roadElements.RoadSection;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;
import fr.enssat.lanniontech.core.trajectory.Trajectory;

import java.util.*;

public class Intersection {

    private Position P;
    private Map<Trajectory, UUID> incomingTrajectories;
    private Map<Trajectory, UUID> outcomingTrajectories;
    private Map<UUID, Map<UUID, SimpleTrajectory>> trajectories;
    //structure is <source ,<destination, AdvancedTrajectory>>

    public Intersection(Position P) {
        this.P = P;
        incomingTrajectories = new HashMap<>();
        outcomingTrajectories = new HashMap<>();
        trajectories = new HashMap<>();
    }

    private void assembleIntersection(UUID id) {
        //TODO compute intersections positions and set them in the trajectories
    }

    public boolean addRoadSection(RoadSection Rs) {
        if (Rs.getA() != P && Rs.getB() != P) {
            return false;
        }
        Lane incomingLane = Rs.getLeftLane(P);
        Lane outcomingLane = Rs.getRightLane(P);
        incomingTrajectories.put(incomingLane.getInsertTrajectory(), Rs.getMyRoad().getId());
        outcomingTrajectories.put(outcomingLane.getInsertTrajectory(), Rs.getMyRoad().getId());
        return true;
    }

    public int getTrajectoriesSize() {
        int s = 0;
        for (UUID uuid : trajectories.keySet()) {
            s += trajectories.get(uuid).size();
        }
        return s;
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
