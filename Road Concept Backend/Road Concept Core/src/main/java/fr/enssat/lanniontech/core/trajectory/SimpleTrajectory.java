package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.trajectory.informations.InformationType;
import fr.enssat.lanniontech.core.trajectory.informations.TrajectoryInformation;
import fr.enssat.lanniontech.core.trajectory.informations.TrajectoryInformator;
import fr.enssat.lanniontech.core.vehicleElements.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SimpleTrajectory extends Trajectory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTrajectory.class);

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


    public SimpleTrajectory(PosFunction pF, double start, double stop, double width, UUID roadId, Position position, double speed) {
        super(roadId, position, speed);
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
    public TrajectoryInformator getInformations(Side side, double distanceOut) {
        TrajectoryInformator informator = new TrajectoryInformator(distanceOut);
        informator.setExplored(this);
        double distance;

        int pos = vehiclesSides.indexOf(side);
        if (pos != vehiclesSides.size() - 1) {
            distance = Math.abs(vehiclesSides.get(pos + 1).getPos() - side.getPos());
            informator.addInformation(
                    new TrajectoryInformation(
                            distance,
                            vehiclesSides.get(pos + 1).getMyVehicle().getSpeed(),
                            InformationType.VEHICLE));
        }

        for (TrajectoryJunction destination : destinationsTrajectories.values()) {
            distance = Math.abs(side.getPos() - destination.getSourcePos());
            if(distance < distanceOut){
                informator.addJunction(distance,destination);
            }else{
                informator.addInformation(
                        new TrajectoryInformation(
                                distance,
                                0,
                                InformationType.FREE));
            }
        }
        informator.computeInformations();

        return informator;
    }

    @Override
    public void getInformations(double pos, double distance, TrajectoryInformator informator) {
        double tempDistance;
        informator.setExplored(this);
        if(!vehiclesSides.isEmpty()){
            Side nearestSide = vehiclesSides.get(0);
            double nearestDistance = Math.abs(pos - nearestSide.getPos());
            for(int i=1; i<vehiclesSides.size();i++){
                tempDistance = Math.abs(pos - vehiclesSides.get(i).getPos());
                if(tempDistance < nearestDistance){
                    nearestDistance = distance;
                    nearestSide = vehiclesSides.get(i);
                }
            }

            if(nearestSide.getLength()<0){
                informator.addInformation(
                        new TrajectoryInformation(
                                distance + nearestDistance,
                                -nearestSide.getMyVehicle().getSpeed(),
                                InformationType.INCOMING_VEHICLE));
            }else {
                informator.addInformation(
                        new TrajectoryInformation(
                                distance + nearestDistance,
                                nearestSide.getMyVehicle().getSpeed(),
                                InformationType.VEHICLE));
            }
        }

        for (TrajectoryJunction source : sourcesTrajectories.values()) {
            tempDistance = Math.abs(source.getDestinationPos() - pos);
            if(tempDistance != 0){
                tempDistance += distance;
                if(tempDistance > informator.getDistanceOut()){
                    informator.addInformation(new TrajectoryInformation(tempDistance,0,InformationType.FREE));
                }else {
                    informator.addJunction(tempDistance,source);
                }
            }
        }

        for (TrajectoryJunction source : sourcesTrajectories.values()) {
            tempDistance = Math.abs(source.getSourcePos() - pos);
            if(tempDistance != 0){
                tempDistance += distance;
                if(tempDistance > informator.getDistanceOut()){
                    informator.addInformation(new TrajectoryInformation(tempDistance,0,InformationType.FREE));
                }else {
                    informator.addJunction(tempDistance,source);
                }
            }
        }
    }

    @Override
    public double getDistanceToFirst(double freeDistance) {
        if (vehiclesSides.isEmpty()) {
            if (freeDistance - length > 0) {
                return length + getNext().getDestination().getDistanceToFirst(freeDistance - length);
            } else {
                return length;
            }
        } else {
            return vehiclesSides.get(0).getPos();
        }
    }

    @Override
    protected double getDistanceToLast(double freeDistance) {
        if (vehiclesSides.isEmpty()) {
            if (freeDistance - length > 0) {
                return length + getPrev().getSource().getDistanceToLast(freeDistance - length);
            } else {
                return length;
            }
        } else {
            return length - vehiclesSides.get(vehiclesSides.size() - 1).getPos();
        }
    }

    @Override
    public double getDistanceToNext(Side side, double freeDistance) {
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            double distance = length - side.getPos();
            if (freeDistance - distance > 0) {
                return distance + getNext().getDestination().getDistanceToFirst(freeDistance - distance);
            } else {
                return distance;
            }
        } else {
            return vehiclesSides.get(pos + 1).getPos() - side.getPos();
        }
    }

    @Override
    public Position getGPS(double pos) {
        Position P;
        if (inverted) {
            P = pF.get(start - pos, width);
        } else {
            P = pF.get(start + pos, width);
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

    @Override
    public List<TrajectoryJunction> getAllNext() {
        List<TrajectoryJunction> destinations = new ArrayList<>();
        for (TrajectoryJunction junction : destinationsTrajectories.values()) {
            destinations.add(junction);
        }
        return destinations;
    }

    @Override
    public TrajectoryJunction getPrev() {
        return defaultIn;
    }
}
