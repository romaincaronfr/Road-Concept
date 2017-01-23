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

    public void addJunctions(Map<Double,TrajectoryJunction> toAdd){
        Iterator<Map.Entry<Double,TrajectoryJunction>> iter = toAdd.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Double,TrajectoryJunction> entry = iter.next();
            if(exploredTrajectories.contains(entry.getValue().getSource()) ||
                    exploredTrajectories.contains(entry.getValue().getDestination())){
                iter.remove();
            }
        }

        junctions.putAll(toAdd);
    }

    public void addJunction(Double distance,TrajectoryJunction junction){
            if(exploredTrajectories.contains(junction.getSource())&&
                    exploredTrajectories.contains(junction.getDestination())) {
                junctions.put(distance,junction);
            }
    }

    public void addInformations(List<TrajectoryInformation> information){
        this.informations.addAll(information);
    }

    public void addInformation(TrajectoryInformation information){
        this.informations.add(information);
    }

    public void computeInformations(){
        while(!junctions.isEmpty()){
            Double key = junctions.firstKey();
            TrajectoryJunction value = junctions.get(key);

            if(!exploredTrajectories.contains(value.getSource())){
                //todo get junction & information
            }

            if(!exploredTrajectories.contains(value.getDestination())){
                //todo get junction & information
            }

            junctions.remove(key);
        }
    }

    public double getDistanceOut() {
        return distanceOut;
    }

    public void setExplored(Trajectory t){
        exploredTrajectories.add(t);
    }
}
