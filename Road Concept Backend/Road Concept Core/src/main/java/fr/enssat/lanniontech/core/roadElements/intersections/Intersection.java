package fr.enssat.lanniontech.core.roadElements.intersections;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.positioning.Trajectory;
import fr.enssat.lanniontech.core.roadElements.RoadSection;

import java.util.HashMap;
import java.util.Map;

public class Intersection {
    private Position P;
    private Map<Integer,RoadSection> roadSections;
    private Map<Integer, Map<Integer, Trajectory>> trajectories;
    //structure is <source ,<destination, Trajectory>>

    public Intersection(Position P) {
        this.P = P;
        roadSections = new HashMap<Integer, RoadSection>();
        trajectories = new HashMap<Integer, Map<Integer, Trajectory>>();
    }

    private void addTrajectories(int id){
        //find the missing trajectories
        Map<Integer, Trajectory> myTrajectories = new HashMap<Integer, Trajectory>();

        for (int i : roadSections.keySet()){
            if(id != i){
                Trajectory T = new Trajectory(roadSections.get(i).getRigthLane(P),
                        roadSections.get(id).getLeftLane(P));
                trajectories.get(i).put(id,T);
                T = new Trajectory(roadSections.get(id).getRigthLane(P),
                        roadSections.get(i).getLeftLane(P));
                myTrajectories.put(i,T);
            }
        }
        trajectories.put(id,myTrajectories);
    }

    public boolean addRoadSection(RoadSection Rs){
        if(Rs.getA()!=P && Rs.getB()!=P){
            return false;
        }
        roadSections.put(Rs.getMyRoad().getId(),Rs);
        if (roadSections.size()>0){
            addTrajectories(Rs.getMyRoad().getId());
        }
        return true;

    }

    public int getTrajectoriesSize(){
        int s = 0;
        for(int i : trajectories.keySet()){
            s += trajectories.get(i).size();
        }
        return s;
    }

    public int getRoadSectionsSize(){
        return roadSections.size();
    }

    public boolean removeRoadSection(int id){
        if (roadSections.containsKey(id)){
            for (int i : trajectories.keySet()){
                trajectories.get(i).remove(id);
            }
            trajectories.remove(id);
            roadSections.remove(id);
        }else{
            return false;
        }
        return true;
    }
}
