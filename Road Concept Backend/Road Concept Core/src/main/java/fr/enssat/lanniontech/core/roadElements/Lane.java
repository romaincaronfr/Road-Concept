package fr.enssat.lanniontech.core.roadElements;

import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;

import java.util.ArrayList;
import java.util.List;

public class Lane {
    private RoadSection myRoadSection;
    private Lane nextLane;
    private double length;
    private double width;
    private List<SimpleTrajectory> trajectories; //rigth side (0) << left side

    Lane(RoadSection myRoadSection, double length) {
        this.myRoadSection = myRoadSection;
        this.length = Math.abs(length);
        width = 3.5;
        trajectories = new ArrayList<>();
        if (length>0) {
            trajectories.add(new SimpleTrajectory(myRoadSection.getFunction(), 0, this.length, width / 2));
        } else {
            trajectories.add(new SimpleTrajectory(myRoadSection.getFunction(), this.length, 0, width / 2));
        }
    }

    public double getLength() {
        return length;
    }

    public Lane getNextLane() {
        return nextLane;
    }

    /**
     * set the next lane and assemble lanes trajectories
     */
    public void setNextLane(Lane nextLane) {
        this.nextLane = nextLane;
        trajectories.get(0).addDestination(nextLane.getInsertTrajectory());
        //todo handle lane with multiple trajectories
    }

    public SimpleTrajectory getInsertTrajectory() {
        return trajectories.get(0);
    }
}
