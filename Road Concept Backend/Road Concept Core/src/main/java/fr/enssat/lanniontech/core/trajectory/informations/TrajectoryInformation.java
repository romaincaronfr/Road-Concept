package fr.enssat.lanniontech.core.trajectory.informations;

public class TrajectoryInformation implements Comparable{
    private double distance;
    private double speed;
    private InformationType type;

    public TrajectoryInformation(double distance,double speed,InformationType type){
        this.distance = distance;
        this.speed = speed;
        this.type = type;
    }

    public double getDistance() {
        return distance;
    }

    public double getSpeed() {
        return speed;
    }

    public InformationType getType() {
        return type;
    }

    public boolean isFree(){
        return type == InformationType.FREE;
    }

    @Override
    public int compareTo(Object o) {
        TrajectoryInformation information = (TrajectoryInformation)o;

        return (int)(distance - information.getDistance());
    }
}
