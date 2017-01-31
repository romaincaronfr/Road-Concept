package fr.enssat.lanniontech.core.roadElements;


public class Congestion {
    private double congestionValue;
    private double congestionPercent;

    public Congestion(double occupiedSpace, double totalSpace) {
        occupiedSpace *= 4;
        this.congestionValue = occupiedSpace;
        this.congestionPercent = 100 * occupiedSpace / totalSpace;
    }

    public double getCongestionValue() {
        return congestionValue;
    }

    public double getCongestionPercent() {
        return congestionPercent;
    }

    public static Congestion max(Congestion congestion, Congestion congestion1) {
        if (congestion.getCongestionValue() > congestion1.getCongestionValue()) {
            return congestion;
        } else {
            return congestion1;
        }
    }
}
