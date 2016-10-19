package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.vehicleElements.Side;

import java.util.ArrayList;

public class SimpleTrajectory extends Trajectory {
    private ArrayList<Trajectory> sourcesTrajectories;
    private ArrayList<Trajectory> destinationsTrajectories;
    private PosFunction pF;
    private double width;
    private double start;
    private double stop;
    private boolean inverted;

    public SimpleTrajectory(PosFunction pF, double start, double stop, double width){
        sourcesTrajectories = new ArrayList<>();
        destinationsTrajectories = new ArrayList<>();
        this.pF = pF;
        this.start = start;
        this.stop = stop;
        this.inverted = start > stop;
        if (inverted){
            length = start - stop;
            this.width = -width;
        }else{
            length = stop - start;
            this.width = width;
        }
    }

    /**
     * this method add a destination trajectory
     * @param t
     */
    public void addDestination(SimpleTrajectory t){
        if(destinationsTrajectories.size() == 0){
            if(pF.cross(t.pF)){
                double Ps[] = pF.getInterPos(t.pF,width,t.width);

                setStop(Ps[0]);
                t.setStart(Ps[1]);

                destinationsTrajectories.add(t);
                t.sourcesTrajectories.add(this);
            }else if(t.getGPS(stop).equals(getGPS(start))){
                destinationsTrajectories.add(t);
                t.sourcesTrajectories.add(this);
            } else{
                //todo add exception
            }
        } else {
            //todo add exception
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
        if (inverted){
            length = start - stop;
        }else{
            length = stop - start;
        }
    }

    public void setStop(double stop) {
        this.stop = stop;
        if (inverted){
            length = start - stop;
        }else{
            length = stop - start;
        }
    }

    //Trajectory class implementation

    public Trajectory getNext(){
        if(destinationsTrajectories.size() == 0){
            return null;
        }else {
            return destinationsTrajectories.get(0);
        }
    }

    public double getSpeedOfFirst() {
        if (vehiclesSides.size() == 0) {
            if (destinationsTrajectories.size() == 0) {
                return 0;
            } else {
                return destinationsTrajectories.get(0).getSpeedOfFirst();
            }
        } else {
            return vehiclesSides.get(0).getMyVehicle().getSpeed();
        }
    }

    public double getNextCarSpeed(Side side) {
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            if (destinationsTrajectories.size() == 0) {
                return 0;
            } else {
                return destinationsTrajectories.get(0).getSpeedOfFirst();
            }
        } else {
            return vehiclesSides.get(pos + 1).getMyVehicle().getSpeed();
        }
    }

    public double getDistanceToFirst() {
        if (vehiclesSides.size() == 0) {
            if (destinationsTrajectories.size() == 0) {
                return length;
            } else {
                return length + destinationsTrajectories.get(0).getDistanceToFirst();
            }
        } else {
            return vehiclesSides.get(0).getPos();
        }
    }

    public double getDistanceToNext(Side side) {
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            if (destinationsTrajectories.size() == 0) {
                return length - side.getPos();
            } else {
                return length - side.getPos() + destinationsTrajectories.get(0).getDistanceToFirst();
            }
        } else {
            return vehiclesSides.get(pos + 1).getPos() - side.getPos();
        }
    }

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

    public Position getGPS(double pos) {
        Position P;
        if(inverted){
            P = pF.get(start-pos,width);
        }else{
            P = pF.get(start+pos,width);
        }
        return P;
    }
}
