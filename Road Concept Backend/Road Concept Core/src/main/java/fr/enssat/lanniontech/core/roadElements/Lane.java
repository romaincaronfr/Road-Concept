package fr.enssat.lanniontech.core.roadElements;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.roadSections.DualWayRoadSection;
import fr.enssat.lanniontech.core.roadElements.roadSections.RoadSection;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;

import java.util.ArrayList;
import java.util.List;

public class Lane {
    private Lane nextLane;
    private double length;
    private double width;
    private List<SimpleTrajectory> trajectories; //rigth side (0) << left side

    public Lane(RoadSection myRoadSection, double length) {
        this.length = Math.abs(length);
        if (myRoadSection instanceof DualWayRoadSection) {
            width = 3.5;
        } else {
            width = 0;
        }
        trajectories = new ArrayList<>();
        if (length > 0) {
            trajectories.add(new SimpleTrajectory(myRoadSection.getFunction(), 0, this.length, width / 2, myRoadSection.getMyRoad().getId(), Position.getMean(myRoadSection.getA(), myRoadSection.getB()), myRoadSection.getMyRoad().getMaxSpeed()));
        } else {
            trajectories.add(new SimpleTrajectory(myRoadSection.getFunction(), this.length, 0, width / 2, myRoadSection.getMyRoad().getId(), Position.getMean(myRoadSection.getA(), myRoadSection.getB()), myRoadSection.getMyRoad().getMaxSpeed()));
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
    }

    public SimpleTrajectory getInsertTrajectory() {
        return trajectories.get(0);
    }

    public double getCongestion() {
        double congestion = 0;
        for (SimpleTrajectory trajectory : trajectories) {
            congestion = Math.max(trajectory.getFreeSpaceRatio(), congestion);
        }
        return congestion;
    }
}
