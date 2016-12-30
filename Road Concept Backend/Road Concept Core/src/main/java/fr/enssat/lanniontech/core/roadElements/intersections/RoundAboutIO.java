package fr.enssat.lanniontech.core.roadElements.intersections;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.roads.RoundAbout;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;
import fr.enssat.lanniontech.core.trajectory.TrajectoryJunction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoundAboutIO extends Intersection {

    private UUID id;

    public RoundAboutIO(Position P, UUID RoundAboutUUID) {
        super(P);
        id = RoundAboutUUID;
    }

    @Override
    public void assembleIntersection() {
        for (SimpleTrajectory source : incomingTrajectories) {
            //create the entry in the trajectories table
            Map<UUID, SimpleTrajectory> myTrajectories = new HashMap<>();
            boolean valid = false;
            for (SimpleTrajectory destination : outgoingTrajectories) {
                if (source.getRoadId() != destination.getRoadId() || source.getRoadId() == id) {
                    valid = true;
                    myTrajectories.put(destination.getRoadId(), destination);

                    TrajectoryJunction junction = TrajectoryJunction.computeJunction(source, destination);

                    source.addDestination(junction);
                    source.setDestIntersection(this);
                    destination.addSource(junction);
                    destination.setSourceIntersection(this);
                }
            }
            this.valid &= valid;
            trajectories.put(source.getRoadId(), myTrajectories);
        }
    }
}
