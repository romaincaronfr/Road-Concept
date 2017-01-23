package fr.enssat.lanniontech.core.trajectory.informations;

import fr.enssat.lanniontech.core.trajectory.Trajectory;
import fr.enssat.lanniontech.core.trajectory.TrajectoryJunction;

import java.util.*;

public class TrajectoryInformator {
    private List<Trajectory> exploredTrajectories;
    private SortedMap<Double, TrajectoryJunction> junctions;
    private SortedSet<TrajectoryInformation> informations;
    private double distanceOut;

    public TrajectoryInformator(double distanceOut) {
        exploredTrajectories = new ArrayList<>();
        junctions = new TreeMap<>();
        informations =new TreeSet<>();
        this.distanceOut = distanceOut;
    }

    public void addJunction(Double distance,TrajectoryJunction junction){
            if(exploredTrajectories.contains(junction.getSource()) &&
                    exploredTrajectories.contains(junction.getDestination())) {
                junctions.put(distance,junction);
            }
    }

    public void addInformation(TrajectoryInformation information){
        this.informations.add(information);
    }

    public void computeInformations(){
        while(!junctions.isEmpty()){
            Double key = junctions.firstKey();
            TrajectoryJunction value = junctions.get(key);

            if(!exploredTrajectories.contains(value.getSource())){
                value.getSource().getInformations(value.getSourcePos(),key,this);
            }

            if(!exploredTrajectories.contains(value.getDestination())){
                value.getDestination().getInformations(value.getDestinationPos(),key,this);
            }

            junctions.remove(key);
        }
        if(informations.isEmpty()){
            informations.add(new TrajectoryInformation(distanceOut,0,InformationType.FREE));
        }
    }

    public double getDistanceOut() {
        return distanceOut;
    }

    public void setExplored(Trajectory t){
        exploredTrajectories.add(t);
    }

    public TrajectoryInformation getNearest() {
        return informations.first();
    }
}
