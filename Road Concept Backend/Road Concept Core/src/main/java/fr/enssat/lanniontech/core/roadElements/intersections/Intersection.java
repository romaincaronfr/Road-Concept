package fr.enssat.lanniontech.core.roadElements.intersections;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.roadElements.roadSections.RoadSection;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;
import fr.enssat.lanniontech.core.trajectory.Trajectory;
import fr.enssat.lanniontech.core.trajectory.TrajectoryJunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Intersection {

    private Position P;
    protected List<SimpleTrajectory> incomingTrajectories;
    protected List<SimpleTrajectory> outgoingTrajectories;
    protected Map<UUID, Map<UUID, SimpleTrajectory>> trajectories;
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
        Lane incomingLane = Rs.getOutputLane(P);
        Lane outgoingLane = Rs.getInputLane(P);
        if(incomingLane != null){
            incomingTrajectories.add(incomingLane.getInsertTrajectory());
        }
        if(outgoingLane != null){
            outgoingTrajectories.add(outgoingLane.getInsertTrajectory());
        }
        return true;
    }

    public void removeRoadSection(RoadSection Rs){
        Lane incomingLane = Rs.getOutputLane(P);
        Lane outgoingLane = Rs.getInputLane(P);
        if(incomingLane != null){
            incomingTrajectories.remove(incomingLane.getInsertTrajectory());
        }
        if(outgoingLane != null){
            outgoingTrajectories.remove(outgoingLane.getInsertTrajectory());
        }
    }

    public int getIncommingSize() {
        return incomingTrajectories.size();
    }

    public int getOutgoingSize(){
        return outgoingTrajectories.size();
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

    public boolean isValid(){
        if(incomingTrajectories.size()==0 || outgoingTrajectories.size()==0){
            return false;
        }

        if(incomingTrajectories.size()==1){
            UUID id = incomingTrajectories.get(0).getRoadId();
            for (Trajectory t : outgoingTrajectories){
                if(t.getRoadId()==id){
                    return false;
                }
            }
        }

        if(outgoingTrajectories.size()==1){
            UUID id = outgoingTrajectories.get(0).getRoadId();
            for (Trajectory t : incomingTrajectories){
                if(t.getRoadId()==id){
                    return false;
                }
            }
        }

        return true;
    }

    public List<UUID> getRoadsUUID(){
        List<UUID> ids = new ArrayList<>();

        for (Trajectory t : incomingTrajectories){
            if(!ids.contains(t.getRoadId())){
                ids.add(t.getRoadId());
            }
        }

        for (Trajectory t : outgoingTrajectories){
            if(!ids.contains(t.getRoadId())){
                ids.add(t.getRoadId());
            }
        }

        return ids;
    }
}
