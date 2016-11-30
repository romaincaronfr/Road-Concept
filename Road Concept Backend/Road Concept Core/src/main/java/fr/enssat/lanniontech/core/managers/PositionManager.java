package fr.enssat.lanniontech.core.managers;

import fr.enssat.lanniontech.core.positioning.Position;

import java.util.ArrayList;
import java.util.List;

public class PositionManager {
    private List<Position> positions;
    private double minLat;
    private double maxLat;
    private double minLon;
    private double maxLon;

    public PositionManager() {
        positions = new ArrayList<>();
        maxLat = -360;
        maxLon = -360;
        minLat = 360;
        minLon = 360;
    }

    public Position addPosition(double lon, double lat) {
        maxLon = Math.max(maxLon, lon);
        maxLat = Math.max(maxLat, lat);
        minLon = Math.min(minLon, lon);
        minLat = Math.min(minLat, lat);
        return addPosition(new Position(lon, lat));
    }

    public Position addPosition(Position pos) {
        int i = 0;
        while (i < positions.size() && !pos.equals(positions.get(i))) {
            i++;
        }
        if (i == positions.size()) {
            positions.add(i, pos);
        }
        return positions.get(i);
    }

    public int getSize() {
        return positions.size();
    }

    public boolean isInRange(Position P) {
        double e = 0.01;
        return P.getLatitude() <= maxLat + e && P.getLatitude() >= minLat - e && P.getLongitude() <= maxLon + e && P.getLongitude() >= minLon - e;
    }
}
