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

    public double nearestScore(){
        if(isFree()){
            return distance / 30;
        }
        double score = distance / Math.abs(speed);
        if(type == InformationType.INCOMING_VEHICLE){
            score *= 5;
        }
        return score;
    }

    @Override
    public int compareTo(Object o) {
        TrajectoryInformation information = (TrajectoryInformation)o;

        return (int)(100*(nearestScore() - information.nearestScore()));
    }
}
