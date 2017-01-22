package fr.enssat.lanniontech.core.trajectory;

import java.util.ArrayList;
import java.util.List;

public class TrajectoryInformation implements Cloneable{
    private double distance;
    private double speed;
    private List<Trajectory> exploredTrajectories;
    private double distanceOut;

    public TrajectoryInformation(double distanceOut){
        this.distanceOut = distanceOut;
        distance = 0;
        speed = 0;
        exploredTrajectories = new ArrayList<>();
    }

    public boolean addToDistance(double d){
        distance+=Math.abs(d);
        return distance < distanceOut;
    }

    public void addToExplored(Trajectory T){
        exploredTrajectories.add(T);
    }

    public boolean isExplored(Trajectory T){
        return exploredTrajectories.contains(T);
    }

    public double getDistance() {
        return distance;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isFree(){
        return distance > distanceOut;
    }

    static TrajectoryInformation getNearest(List<TrajectoryInformation> Infos){
        TrajectoryInformation nearest = Infos.get(0);
        for (int i = 1; i < Infos.size(); i++) {
            if(nearest.getDistance() > Infos.get(i).getDistance()){
                nearest = Infos.get(i);
            }
        }
        return nearest;
    }

    public TrajectoryInformation clone(){
        TrajectoryInformation T = new TrajectoryInformation(this.distanceOut);
        T.addToDistance(distance);
        for (Trajectory trajectory : exploredTrajectories) {
            T.addToExplored(trajectory);
        }
        return T;
    }
}
