package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.vehicleElements.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SimpleTrajectory extends Trajectory {

    public static Logger LOG = LoggerFactory.getLogger(SimpleTrajectory.class);

    private SortedMap<Double,Trajectory> sourcesTrajectories;
    private TrajectoryEndType sourceType;
    private SortedMap<Double,Trajectory> destinationsTrajectories;
    private TrajectoryEndType destinationType;
    private PosFunction pF;
    private double width;
    private double start;
    private double stop;
    private boolean inverted;

    public SimpleTrajectory(PosFunction pF, double start, double stop, double width, UUID roadId) {
        super(roadId);
        sourcesTrajectories = new TreeMap<>();
        destinationsTrajectories = new TreeMap<>();
        sourceType = TrajectoryEndType.UNDEFINED;
        destinationType = TrajectoryEndType.UNDEFINED;
        this.pF = pF;
        this.start=start;
        this.stop=stop;
        inverted = start > stop;
        if (inverted) {
            length = start - stop;
            this.width = -width;
        } else {
            length = stop - start;
            this.width = width;
        }
        sourcesTrajectories.put(start,null);
        destinationsTrajectories.put(stop,null);
    }

    /**
     * this method add a destination trajectory
     */
    public void addSource(double position, SimpleTrajectory t) {
        setSourceType(TrajectoryEndType.SIMPLE);
        sourcesTrajectories.put(position,t);
    }

    public void addDestination(double position, SimpleTrajectory t) {
        setSourceType(TrajectoryEndType.SIMPLE);
        sourcesTrajectories.put(position,t);
    }

    public void addSource(double position,EndRoadTrajectory t){
        setSourceType(TrajectoryEndType.DEAD_END);
        sourcesTrajectories.put(position,t);
    }

    public void addDestination(double position, EndRoadTrajectory t){
        setDestinationType(TrajectoryEndType.DEAD_END);
        destinationsTrajectories.put(position,t);
    }

    public double getStart() {
        return start;
    }

    public double getStop() {
        return stop;
    }

    public void setStart(double start) {
        this.start = start;
        if (inverted) {
            length = start - stop;
        } else {
            length = stop - start;
        }
    }

    public void setStop(double stop) {
        this.stop = stop;
        if (inverted) {
            length = start - stop;
        } else {
            length = stop - start;
        }
    }

    public double getWidth() {
        return width;
    }

    public boolean isInverted() {
        return inverted;
    }

    //Trajectory class implementation

    @Override
    public Trajectory getNext() {
        if (destinationsTrajectories.size() == 0) {
            return null;
        } else {
            return (Trajectory) destinationsTrajectories.values().toArray()[0];
        }
    }

    @Override
    public Trajectory getNext(UUID destination) {
        if(destinationsTrajectories.containsKey(destination)){
            return destinationsTrajectories.get(destination);
        }else{
            return getNext();
        }
    }

    @Override
    public double getSpeedOfFirst() {
        if (vehiclesSides.size() == 0) {
            if (getDestinationsTrajectories().size() == 0) {
                return 0;
            } else {
                return getNext().getSpeedOfFirst();
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
                return getNext().getSpeedOfFirst();
            }
        } else {
            return vehiclesSides.get(pos + 1).getMyVehicle().getSpeed();
        }
    }

    @Override
    public double getDistanceToFirst() {
        if (vehiclesSides.size() == 0) {
            if (getDestinationsTrajectories().size() == 0) {
                return length;
            } else {
                return length + getNext().getDistanceToFirst();
            }
        } else {
            return vehiclesSides.get(0).getPos();
        }
    }

    @Override
    public double getDistanceToNext(Side side) {
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            if (getDestinationsTrajectories().size() == 0) {
                return length - side.getPos();
            } else {
                return length - side.getPos() + getNext().getDistanceToFirst();
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

    public Map<Double,Trajectory> getSourcesTrajectories() {
        return sourcesTrajectories;
    }

    public TrajectoryEndType getSourceType() {
        return sourceType;
    }

    public void setSourceType(TrajectoryEndType sourceType) {
        this.sourceType = sourceType;
    }

    public Map<Double,Trajectory> getDestinationsTrajectories() {
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

    public void explore(Map<Trajectory,Boolean> trajectoryMap){
        if(!trajectoryMap.get(this)){
            trajectoryMap.replace(this,true);
            for (Trajectory trajectory : destinationsTrajectories.values()){
                trajectory.explore(trajectoryMap);
            }
        }
    }

    @Override
    public Intersection getNextIntersection() {
        if(destinationType == TrajectoryEndType.INTERSECTION){
            return null;
        }else {
            return getNext().getNextIntersection();
        }
    }
}
