package fr.enssat.lanniontech.core.roadElements.intersections;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.roadElements.RoadSection;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;
import fr.enssat.lanniontech.core.trajectory.TrajectoryJunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Intersection {

    private Position P;
    private List<SimpleTrajectory> incomingTrajectories;
    private List<SimpleTrajectory> outgoingTrajectories;
    private Map<UUID, Map<UUID, SimpleTrajectory>> trajectories;
    //structure is <source ,<destination, destinationTrajectory>>

    public Intersection(Position P) {
        this.P = P;
        incomingTrajectories = new ArrayList<>();
        outgoingTrajectories = new ArrayList<>();
        trajectories = new HashMap<>();
    }

    /**
     * assemble all the incoming trajectories to the outgoing trajectories when their roadId are different
     */
    public void assembleIntersection() {
        for (SimpleTrajectory source : incomingTrajectories) {
            //create the entry in the trajectories table
            Map<UUID, SimpleTrajectory> myTrajectories = new HashMap<>();
            for (SimpleTrajectory destination : outgoingTrajectories) {
                if (source.getRoadId() != destination.getRoadId()) {
                    myTrajectories.put(destination.getRoadId(), destination);

                    TrajectoryJunction junction = TrajectoryJunction.computeJunction(source, destination);

                    source.addDestination(junction);
                    source.setDestIntersection(this);
                    destination.addSource(junction);
                    destination.setSourceIntersection(this);
                }
            }
            trajectories.put(source.getRoadId(), myTrajectories);
        }
    }

    /**
     * add the passed Roadsection to the intersection
     */
    public boolean addRoadSection(RoadSection Rs) {
        if (Rs.getA() != P && Rs.getB() != P) {
            return false;
        }
        Lane incomingLane = Rs.getLeftLane(P);
        Lane outgoingLane = Rs.getRightLane(P);
        incomingTrajectories.add(incomingLane.getInsertTrajectory());
        outgoingTrajectories.add(outgoingLane.getInsertTrajectory());
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
        for (Map<UUID, SimpleTrajectory> map : this.trajectories.values()) {
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
