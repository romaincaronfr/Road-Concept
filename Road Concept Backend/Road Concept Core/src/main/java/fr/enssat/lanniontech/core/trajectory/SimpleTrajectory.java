package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.vehicleElements.Side;

import java.util.ArrayList;
import java.util.List;

public class SimpleTrajectory extends Trajectory {
    private List<Trajectory> sourcesTrajectories;
    private TrajectoryType sourceType;
    private List<Trajectory> destinationsTrajectories;
    private TrajectoryType destinationType;
    private PosFunction pF;
    private double width;
    private double start;
    private double stop;
    private boolean inverted;

    public SimpleTrajectory(PosFunction pF, double start, double stop, double width) {
        setSourcesTrajectories(new ArrayList<>());
        setDestinationsTrajectories(new ArrayList<>());
        setSourceType(TrajectoryType.Undefined);
        setDestinationType(TrajectoryType.Undefined);
        this.setpF(pF);
        this.setStart(start);
        this.setStop(stop);
        this.setInverted(start > stop);
        if (isInverted()) {
            length = start - stop;
            this.setWidth(-width);
        } else {
            length = stop - start;
            this.setWidth(width);
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

                getDestinationsTrajectories().add(t);
                t.getSourcesTrajectories().add(this);

                t.setSourceType(TrajectoryType.SimpleTrajectory);
                setSourceType(TrajectoryType.SimpleTrajectory);
            } else if (t.getGPS(getStop()).equals(getGPS(getStart()))) {
                getDestinationsTrajectories().add(t);
                t.getSourcesTrajectories().add(this);
            }
        }
    }

    public void addDestination(AdvancedTrajectory t) {
        if (getDestinationType() != TrajectoryType.SimpleTrajectory) {
            getDestinationsTrajectories().add(t);
            if (isInverted()) {
                setStop(getStop() + t.getSecurityDistance());
            } else {
                setStop(getStop() - t.getSecurityDistance());
            }
            setDestinationType(TrajectoryType.AdvancedTrajectory);
        }
    }

    public void addSource(AdvancedTrajectory t) {
        if (getSourceType() != TrajectoryType.SimpleTrajectory) {
            getSourcesTrajectories().add(t);
            if (isInverted()) {
                setStart(getStart() - t.getSecurityDistance());
            } else {
                setStart(getStart() + t.getSecurityDistance());
            }
            setSourceType(TrajectoryType.AdvancedTrajectory);
        }
    }

    public double getStart() {
        return start;
    }

    public double getStop() {
        return stop;
    }

    public void setStart(double start) {
        this.start = start;
        if (isInverted()) {
            length = start - getStop();
        } else {
            length = getStop() - start;
        }
    }

    public void setStop(double stop) {
        this.stop = stop;
        if (isInverted()) {
            length = getStart() - stop;
        } else {
            length = stop - getStart();
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
        if (getDestinationsTrajectories().size() == 0) {
            return null;
        } else {
            return getDestinationsTrajectories().get(0);
        }
    }

    @Override
    public double getSpeedOfFirst() {
        if (vehiclesSides.size() == 0) {
            if (getDestinationsTrajectories().size() == 0) {
                return 0;
            } else {
                return getDestinationsTrajectories().get(0).getSpeedOfFirst();
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
                return getDestinationsTrajectories().get(0).getSpeedOfFirst();
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
                return length + getDestinationsTrajectories().get(0).getDistanceToFirst();
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
                return length - side.getPos() + getDestinationsTrajectories().get(0).getDistanceToFirst();
            }
        } else {
            return vehiclesSides.get(pos + 1).getPos() - side.getPos();
        }
    }

    @Override
    public boolean rangeIsFree(double start, double end) {
        int i = 0;
        double pos;
        while (i < vehiclesSides.size()) {
            pos = vehiclesSides.get(i).getPos();
            if (pos >= start && pos <= end) {
                return false;
            } else if (pos > end) {
                return true;
            }
            i++;
        }
        return true;
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

    public List<Trajectory> getSourcesTrajectories() {
        return sourcesTrajectories;
    }

    public void setSourcesTrajectories(List<Trajectory> sourcesTrajectories) {
        this.sourcesTrajectories = sourcesTrajectories;
    }

    public TrajectoryType getSourceType() {
        return sourceType;
    }

    public void setSourceType(TrajectoryType sourceType) {
        this.sourceType = sourceType;
    }

    public List<Trajectory> getDestinationsTrajectories() {
        return destinationsTrajectories;
    }

    public void setDestinationsTrajectories(List<Trajectory> destinationsTrajectories) {
        this.destinationsTrajectories = destinationsTrajectories;
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
