package fr.enssat.lanniontech;

public class Road {
    protected Position A;
    protected Position B;
    protected double length;

    Road(Position A, Position B) {
        this.A = A;
        this.B = B;
        this.length = Position.length(A,B);

    }
}
