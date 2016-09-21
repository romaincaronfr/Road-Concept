package fr.enssat.lanniontech;

import java.util.ArrayList;
import java.util.prefs.BackingStoreException;

public class Lane {
    private Lane nextLane;
    private Lane prevLane;
    private ArrayList<FrontBackSide> vehiclesSides;
    private double length;

    Lane(double length){
        this.length = length;
        nextLane = this;
        prevLane = this;
        vehiclesSides = new ArrayList<FrontBackSide>();
    }

    public double getLength() {
        return length;
    }

    public void getOut(FrontBackSide side) {
        vehiclesSides.remove(side);
    }

    public void getIn(FrontBackSide side) {
        int i = 0;
        System.out.println(vehiclesSides.size());
        while(i<vehiclesSides.size()&&side.pos>vehiclesSides.get(i).pos)
        {
            i++;
        }
        vehiclesSides.add(i,side);
    }

    public Lane getNextLane(){
        return nextLane;
    }
}
