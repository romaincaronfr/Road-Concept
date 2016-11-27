package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.vehicleElements.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleTrajectory extends Trajectory {

    public static Logger LOG = LoggerFactory.getLogger(SimpleTrajectory.class);

    private Map<UUID, TrajectoryJunction> sourcesTrajectories;
    private TrajectoryEndType sourceType;
    private Map<UUID, TrajectoryJunction> destinationsTrajectories;
    private TrajectoryJunction defaultIn;
    private TrajectoryJunction defaultOut;
    private TrajectoryEndType destinationType;
    private PosFunction pF;
    private double width;
    private double start;
    private double stop;
    private boolean inverted;
    private Intersection sourceIntersection;
    private Intersection destIntersection;


    public SimpleTrajectory(PosFunction pF, double start, double stop, double width, UUID roadId) {
        super(roadId);
        sourcesTrajectories = new HashMap<>();
        destinationsTrajectories = new HashMap<>();
        sourceType = TrajectoryEndType.UNDEFINED;
        destinationType = TrajectoryEndType.UNDEFINED;
        this.pF = pF;
        this.start = start;
        this.stop = stop;
        inverted = start > stop;
        if (inverted) {
            length = start - stop;
            this.width = -width;
        } else {
            length = stop - start;
            this.width = width;
        }
        sourcesTrajectories.put(null, new TrajectoryJunction(null, this, 0, start));
        destinationsTrajectories.put(null, new TrajectoryJunction(this, null, stop, 0));
    }

    /**
     * this method add a destination trajectory
     */
    public void addSource(TrajectoryJunction t) {
        sourcesTrajectories.remove(null);
        sourcesTrajectories.put(t.getSource().getRoadId(), t);
        defaultIn = t;
    }

    public void addDestination(TrajectoryJunction t) {
        destinationsTrajectories.remove(null);
        destinationsTrajectories.put(t.getDestination().getRoadId(), t);
        defaultOut = t;
    }

    /**
     * return the default start position
     */
    public double getStart() {
        return start;
    }

    /**
     * return the default stop position
     */
    public double getStop() {
        return stop;
    }

    public double getWidth() {
        return width;
    }

    public boolean isInverted() {
        return inverted;
    }

    //Trajectory class implementation

    @Override
    public TrajectoryJunction getNext() {
        return defaultOut;
    }

    @Override
    public TrajectoryJunction getNext(UUID destination) {
        return destinationsTrajectories.getOrDefault(destination, defaultOut);
    }

    @Override
    public double getSpeedOfFirst() {
        if (vehiclesSides.size() == 0) {
            if (getDestinationsTrajectories().size() == 0) {
                return 0;
            } else {
                return getNext().getDestination().getSpeedOfFirst();
            }
        } else {
            return vehiclesSides.get(0).getMyVehicle().getSpeed();
        }
    }

    @Override
    public double getNextCarSpeed(Side side) {
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            if (getDestinationsTrajectories().size() == 0) {
                return 0;
            } else {
                return getNext().getDestination().getSpeedOfFirst();
            }
        } else {
            return vehiclesSides.get(pos + 1).getMyVehicle().getSpeed();
        }
    }

    @Override
    public double getDistanceToFirst(double freeDistance) {
        if (vehiclesSides.size() == 0) {
            if(freeDistance - length > 0) {
                return length + getNext().getDestination().getDistanceToFirst(freeDistance);
            }else{
                return length;
            }
        } else {
            return vehiclesSides.get(0).getPos();
        }
    }

    @Override
    public double getDistanceToNext(Side side, double freeDistance) {
        if(freeDistance < 0){
            return freeDistance;
        }
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            double distance = length - side.getPos();
            if(freeDistance - distance > 0){
                return distance + getNext().getDestination().getDistanceToFirst(freeDistance - distance);
            }else{
                return distance;
            }
        } else {
            return vehiclesSides.get(pos + 1).getPos() - side.getPos();
        }
    }

    @Override
    public Position getGPS(double pos) {
        Position P;
        if (isInverted()) {
            P = getpF().get(getStart() - pos, getWidth());
        } else {
            P = getpF().get(getStart() + pos, getWidth());
        }
        return P;
    }

    public Map<UUID, TrajectoryJunction> getSourcesTrajectories() {
        return sourcesTrajectories;
    }

    public TrajectoryEndType getSourceType() {
        return sourceType;
    }

    public void setSourceType(TrajectoryEndType sourceType) {
        this.sourceType = sourceType;
    }

    public Map<UUID, TrajectoryJunction> getDestinationsTrajectories() {
        return destinationsTrajectories;
    }

    public TrajectoryEndType getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(TrajectoryEndType destinationType) {
        this.destinationType = destinationType;
    }

    public PosFunction getpF() {
        return pF;
    }

    @Override
    public void explore(Map<Trajectory, Boolean> trajectoryMap) {
        if (!trajectoryMap.get(this)) {
            trajectoryMap.replace(this, true);
            for (TrajectoryJunction trajectory : destinationsTrajectories.values()) {
                trajectory.getDestination().explore(trajectoryMap);
            }
        }
    }

    public void setSourceIntersection(Intersection sourceIntersection) {
        sourceType = TrajectoryEndType.INTERSECTION;
        this.sourceIntersection = sourceIntersection;
    }

    public void setDestIntersection(Intersection destIntersection) {
        destinationType = TrajectoryEndType.INTERSECTION;
        this.destIntersection = destIntersection;
    }

    @Override
    public Intersection getNextIntersection() {
        if (destinationType == TrajectoryEndType.INTERSECTION) {
            return destIntersection;
        } else {
            return getNext().getDestination().getNextIntersection();
        }
    }
}
