package fr.enssat.lanniontech.core.roadElements.intersections;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.RoadSection;
import fr.enssat.lanniontech.core.trajectory.AdvancedTrajectory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Intersection {

    private Position P;
    private Map<UUID, RoadSection> roadSections;
    private Map<UUID, Map<UUID, AdvancedTrajectory>> trajectories;
    //structure is <source ,<destination, AdvancedTrajectory>>

    public Intersection(Position P) {
        this.P = P;
        roadSections = new HashMap<>();
        trajectories = new HashMap<>();
    }

    private void addTrajectories(UUID id) {
        //find the missing trajectories
        Map<UUID, AdvancedTrajectory> myTrajectories = new HashMap<>();

        for (UUID uuid : roadSections.keySet()) {
            if (id != uuid) {
                AdvancedTrajectory T = new AdvancedTrajectory(roadSections.get(uuid).getLeftLane(P).getInsertTrajectory(), roadSections.get(id).getRightLane(P).getInsertTrajectory());
                trajectories.get(uuid).put(id, T);

                T = new AdvancedTrajectory(roadSections.get(id).getLeftLane(P).getInsertTrajectory(), roadSections.get(uuid).getRightLane(P).getInsertTrajectory());
                myTrajectories.put(uuid, T);
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
        for (UUID uuid : trajectories.keySet()) {
            s += trajectories.get(uuid).size();
        }
        return s;
    }

    public int getRoadSectionsSize() {
        return roadSections.size();
    }

    public boolean removeRoadSection(UUID id) {
        if (roadSections.containsKey(id)) {
            for (UUID uuid : trajectories.keySet()) {
                trajectories.get(uuid).remove(id);
            }
            trajectories.remove(id);
            roadSections.remove(id);
        } else {
            return false;
        }
        return true;
    }
}
