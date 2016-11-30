package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.vehicleElements.Side;

import java.util.Map;
import java.util.UUID;

public class EndRoadTrajectory extends Trajectory {
    private TrajectoryJunction destination;
    private TrajectoryJunction source;
    private PosFunction pf;

    public EndRoadTrajectory(SimpleTrajectory source, SimpleTrajectory destination, UUID roadId) {
        super(roadId);

        Position A = source.getGPS(source.getStop());
        Position B = destination.getGPS(0);
        pf = new PosFunction(A, B);
        length = Position.length(A, B);
    }

    //------------inherited methods----------//


    public TrajectoryJunction getNext() {
        return destination;
    }

    @Override
    public TrajectoryJunction getNext(UUID destination) {
        return getNext();
    }

    public double getSpeedOfFirst() {
        if (vehiclesSides.size() == 0) {
            return destination.getDestination().getSpeedOfFirst();
        } else {
            return vehiclesSides.get(0).getMyVehicle().getSpeed();
        }
    }

    public double getNextCarSpeed(Side side) {
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            return destination.getDestination().getSpeedOfFirst();
        } else {
            return vehiclesSides.get(pos + 1).getMyVehicle().getSpeed();
        }
    }

    public double getDistanceToFirst(double freeDistance) {
        if (vehiclesSides.size() == 0) {
            if (freeDistance - length > 0) {
                return length + destination.getDestination().getDistanceToFirst(freeDistance - length);
            } else {
                return length;
            }
        } else {
            return vehiclesSides.get(0).getPos();
        }
    }

    public double getDistanceToNext(Side side, double freeDistance) {
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            double distance = length - side.getPos();
            if (freeDistance - distance > 0) {
                return length - side.getPos() + destination.getDestination().getDistanceToFirst(freeDistance - distance);
            } else {
                return distance;
            }
        } else {
            return vehiclesSides.get(pos + 1).getPos() - side.getPos();
        }
    }

    public Position getGPS(double pos) {
        return pf.get(pos);
    }

    public TrajectoryJunction getSource() {
        return source;
    }

    public TrajectoryJunction getDestination() {
        return destination;
    }

    public void setDestination(TrajectoryJunction destination) {
        this.destination = destination;
    }

    public void setSource(TrajectoryJunction source) {
        this.source = source;
    }

    public void explore(Map<Trajectory, Boolean> trajectoryMap) {
        if (!trajectoryMap.get(this)) {
            trajectoryMap.replace(this, true);
            this.destination.getDestination().explore(trajectoryMap);
        }
    }

    @Override
    public Intersection getNextIntersection() {
        return destination.getDestination().getNextIntersection();
    }
}
