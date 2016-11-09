package fr.enssat.lanniontech.core.managers;

import fr.enssat.lanniontech.core.positioning.Position;

import java.util.ArrayList;
import java.util.List;

public class PositionManager {
    private List<Position> positions;

    public PositionManager() {
        positions = new ArrayList<>();
    }

    public Position addPosition(double lat, double lon) {
        return addPosition(new Position(lat, lon));
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
}
