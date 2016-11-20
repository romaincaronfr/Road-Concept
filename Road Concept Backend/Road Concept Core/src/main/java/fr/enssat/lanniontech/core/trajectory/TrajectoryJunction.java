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
}
