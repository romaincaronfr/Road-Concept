package fr.enssat.lanniontech.core.pathFinding;


import fr.enssat.lanniontech.core.managers.RoadManager;
import fr.enssat.lanniontech.core.trajectory.Trajectory;

import java.util.Random;

public class PathFinder {
    private RoadManager roadManager;
    private Random gen;

    public PathFinder(RoadManager RM){
        roadManager = RM;
        gen = new Random();
    }

    public Path getRandomPath(Trajectory source, int length){
        Path myPath = new Path();
        //todo generate path
        return myPath;
    }
}
