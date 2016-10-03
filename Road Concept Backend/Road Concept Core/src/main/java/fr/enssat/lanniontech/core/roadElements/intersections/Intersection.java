package fr.enssat.lanniontech.core.roadElements.intersections;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Road;
import fr.enssat.lanniontech.core.positioning.Trajectory;


import java.util.Map;

public class Intersection {
    Position P;
    Map<Road,Map<Road,Trajectory>> trajectories;

    public Intersection(Position P){
        this.P = P;
    }
}
