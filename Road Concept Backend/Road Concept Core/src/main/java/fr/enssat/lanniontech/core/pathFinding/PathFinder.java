package fr.enssat.lanniontech.core.pathFinding;


import fr.enssat.lanniontech.core.managers.RoadManager;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.trajectory.AdvancedTrajectory;
import fr.enssat.lanniontech.core.trajectory.Trajectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PathFinder {

    public static Logger LOG = LoggerFactory.getLogger(PathFinder.class);

    private RoadManager roadManager;
    private Random gen;

    public PathFinder(RoadManager RM){
        roadManager = RM;
        gen = new Random();
    }

    public Path getRandomPath(Trajectory source, int length){
        Path myPath = new Path();
        UUID step = source.getRoadId();
        LOG.debug("starting road : " + step);
        myPath.addToPath(step);
        Intersection nextInter = source.getNextIntersection();
        List<AdvancedTrajectory> possibleNext;

        for (int i = 0; i < length; i++) {
            possibleNext = nextInter.getTrajectoriesFrom(step);

            if(possibleNext.size()==1){
                step=possibleNext.get(0).getRoadId();
                nextInter = possibleNext.get(0).getNextIntersection();
            }else{
                int next = gen.nextInt(possibleNext.size());
                step=possibleNext.get(next).getRoadId();
                nextInter = possibleNext.get(next).getNextIntersection();
            }
            LOG.debug("next road : " + step);
        }

        return myPath;
    }
}
