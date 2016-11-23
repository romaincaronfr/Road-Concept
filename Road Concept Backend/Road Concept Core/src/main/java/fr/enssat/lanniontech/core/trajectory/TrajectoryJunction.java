package fr.enssat.lanniontech.core.trajectory;

public class TrajectoryJunction {
    private Trajectory source;
    private Trajectory destination;
    private double sourcePos;
    private double destinationPos;

    public TrajectoryJunction(Trajectory source, Trajectory destination,
                              double sourcePos, double destinationPos) {
        this.source = source;
        this.destination = destination;
        this.sourcePos = sourcePos;
        this.destinationPos = destinationPos;
    }

    public Trajectory getSource() {
        return source;
    }

    public Trajectory getDestination() {
        return destination;
    }

    public double getSourcePos() {
        return sourcePos;
    }

    public double getDestinationPos() {
        return destinationPos;
    }

    public static TrajectoryJunction computeJunction(SimpleTrajectory source, SimpleTrajectory destination) {
        TrajectoryJunction junction;

        if (source.getpF().cross(destination.getpF())) {
            double Ps[] = source.getpF().getInterPos(destination.getpF(),
                    source.getWidth(), destination.getWidth());
            junction = new TrajectoryJunction(source, destination, Ps[0], Ps[1]);
        } else {
            junction = new TrajectoryJunction(source, destination, source.getStop(), destination.getStart());
        }
        return junction;
    }
}
