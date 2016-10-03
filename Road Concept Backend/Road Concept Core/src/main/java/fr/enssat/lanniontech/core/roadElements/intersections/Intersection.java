package fr.enssat.lanniontech.roadElements.intersections;

import fr.enssat.lanniontech.positioning.Position;
import fr.enssat.lanniontech.positioning.Trajectory;
import fr.enssat.lanniontech.roadElements.Road;

import java.util.Map;

public class Intersection {
    Position P;
    Map<Road,Map<Road,Trajectory>> trajectories;

    public Intersection(){

    }
}
