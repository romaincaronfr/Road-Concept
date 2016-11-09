package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.vehicleElements.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SimpleTrajectory extends Trajectory {

    public static Logger LOG = LoggerFactory.getLogger(SimpleTrajectory.class);

    private Map<UUID,Trajectory> sourcesTrajectories;
    private TrajectoryType sourceType;
    private Map<UUID,Trajectory> destinationsTrajectories;
    private TrajectoryType destinationType;
    private PosFunction pF;
    private double width;
    private double start;
    private double stop;
    private boolean inverted;

    public SimpleTrajectory(PosFunction pF, double start, double stop, double width, UUID roadId) {
        super(roadId);
        sourcesTrajectories = new HashMap<>();
        destinationsTrajectories = new HashMap<>();
        sourceType = TrajectoryType.Undefined;
        destinationType = TrajectoryType.Undefined;
        this.setpF(pF);
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

    }

    /**
     * this method add a destination trajectory
     */
    public void addDestination(SimpleTrajectory t) {
        if (getDestinationType() == TrajectoryType.Undefined) {
            if (getpF().cross(t.getpF())) {
                double Ps[] = getpF().getInterPos(t.getpF(), getWidth(), t.getWidth());

                setStop(Ps[0]);
                t.setStart(Ps[1]);

                destinationsTrajectories.put(t.getRoadId(),t);
                t.getSourcesTrajectories().put(getRoadId(),this);

                t.setSourceType(TrajectoryType.SimpleTrajectory);
                setSourceType(TrajectoryType.SimpleTrajectory);
            } else if (t.getGPS(getStop()).equals(getGPS(getStart()))) {
                getDestinationsTrajectories().put(t.getRoadId(),t);
                t.getSourcesTrajectories().put(getRoadId(),this);
            }
            destinationType = TrajectoryType.SimpleTrajectory;
        }
    }

    public void addDestination(AdvancedTrajectory t) {
        if (destinationType == TrajectoryType.Undefined) {
            if (inverted) {
                setStop(stop + t.getSecurityDistance());
            } else {
                setStop(stop - t.getSecurityDistance());
            }
            destinationType = TrajectoryType.AdvancedTrajectory;
            destinationsTrajectories.put(t.getRoadId(),t);
        }else if(destinationType == TrajectoryType.AdvancedTrajectory){
            destinationsTrajectories.put(t.getRoadId(),t);
        }
    }

    public void addSource(AdvancedTrajectory t) {
        if (sourceType == TrajectoryType.Undefined) {
            if (isInverted()) {
                setStart(start - t.getSecurityDistance());
            } else {
                setStart(start + t.getSecurityDistance());
            }
            sourceType = TrajectoryType.AdvancedTrajectory;
            sourcesTrajectories.put(t.getRoadId(),t);
        }else if(sourceType == TrajectoryType.AdvancedTrajectory){
            sourcesTrajectories.put(t.getRoadId(),t);
        }
    }

    public void addSource(EndRoadTrajectory t){
        setSourceType(TrajectoryType.EndRoadTrajectory);
        sourcesTrajectories.put(t.getRoadId(),t);
    }

    public void addDestination(EndRoadTrajectory t){
        setDestinationType(TrajectoryType.EndRoadTrajectory);
        destinationsTrajectories.put(t.getRoadId(),t);
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

    public PosFunction getFunction() {
        return getpF();
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

    public Map<UUID,Trajectory> getSourcesTrajectories() {
        return sourcesTrajectories;
    }

    public TrajectoryType getSourceType() {
        return sourceType;
    }

    public void setSourceType(TrajectoryType sourceType) {
        this.sourceType = sourceType;
    }

    public Map<UUID,Trajectory> getDestinationsTrajectories() {
        return destinationsTrajectories;
    }

    public TrajectoryType getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(TrajectoryType destinationType) {
        this.destinationType = destinationType;
    }

    public PosFunction getpF() {
        return pF;
    }

    public void setpF(PosFunction pF) {
        this.pF = pF;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
}
