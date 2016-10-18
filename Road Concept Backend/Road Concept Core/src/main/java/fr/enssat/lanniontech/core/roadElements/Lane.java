package fr.enssat.lanniontech.core.roadElements;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;
import fr.enssat.lanniontech.core.trajectory.Trajectory;

import java.util.ArrayList;

public class Lane {
    private RoadSection myRoadSection;
    private Lane nextLane;
    private double length;
    private double width;
    private ArrayList<SimpleTrajectory> trajectories; //rigth side (0) << left side

    Lane(RoadSection myRoadSection, double length) {
        this.myRoadSection = myRoadSection;
        this.length = length;
        this.nextLane = null;
        trajectories = new ArrayList<>();
        width = 3.5;
    }

    public double getLength() {
        return length;
    }

    public double getMyWPos() {
        return myRoadSection.getWPos(this, width);
    }

    public RoadSection getMyRoadSection() {
        return myRoadSection;
    }

    public Lane getNextLane() {
        return nextLane;
    }

    public void setNextLane(Lane nextLane) {
        this.nextLane = nextLane;
        //todo assemble trajectories
    }

    @Deprecated
    public Position getPosition(double pos) {
        return myRoadSection.getPosition(this, pos, width / 2);
    }


    public Trajectory getInsertTrajectory() {
        return trajectories.get(0);
    }
}
