package fr.enssat.lanniontech.core.roadElements.intersections;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.RoadSection;
import fr.enssat.lanniontech.core.trajectory.AdvancedTrajectory;

import java.util.HashMap;
import java.util.Map;

public class Intersection {
    private Position P;
    private Map<Integer, RoadSection> roadSections;
    private Map<Integer, Map<Integer, AdvancedTrajectory>> trajectories;
    //structure is <source ,<destination, AdvancedTrajectory>>

    public Intersection(Position P) {
        this.P = P;
        roadSections = new HashMap<Integer, RoadSection>();
        trajectories = new HashMap<Integer, Map<Integer, AdvancedTrajectory>>();
    }

    private void addTrajectories(int id) {
        //find the missing trajectories
        Map<Integer, AdvancedTrajectory> myTrajectories = new HashMap<>();

        for (int i : roadSections.keySet()) {
            if (id != i) {
                AdvancedTrajectory T = new AdvancedTrajectory(roadSections.get(i).getRightLane(P).getInsertTrajectory(),
                        roadSections.get(id).getLeftLane(P).getInsertTrajectory());

                trajectories.get(i).put(id, T);
                T = new AdvancedTrajectory(roadSections.get(id).getRightLane(P).getInsertTrajectory(),
                        roadSections.get(i).getLeftLane(P).getInsertTrajectory());
                myTrajectories.put(i, T);
            }
        }
        trajectories.put(id, myTrajectories);
    }

    public boolean addRoadSection(RoadSection Rs) {
        if (Rs.getA() != P && Rs.getB() != P) {
            return false;
        }
        roadSections.put(Rs.getMyRoad().getId(), Rs);
        addTrajectories(Rs.getMyRoad().getId());
        return true;
    }

    public int getTrajectoriesSize() {
        int s = 0;
        for (int i : trajectories.keySet()) {
            s += trajectories.get(i).size();
        }
        return s;
    }

    public int getRoadSectionsSize() {
        return roadSections.size();
    }

    public boolean removeRoadSection(int id) {
        if (roadSections.containsKey(id)) {
            for (int i : trajectories.keySet()) {
                trajectories.get(i).remove(id);
            }
            trajectories.remove(id);
            roadSections.remove(id);
        } else {
            return false;
        }
        return true;
    }
}
