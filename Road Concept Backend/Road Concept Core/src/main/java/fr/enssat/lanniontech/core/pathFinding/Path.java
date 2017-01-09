package fr.enssat.lanniontech.core.pathFinding;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Path {
    private List<UUID> path;

    public Path() {
        path = new ArrayList<>();
    }

    public void addToPath(UUID dest) {
        if (path.isEmpty() || path.get(path.size() - 1) != dest) {
            path.add(dest);
        }
    }

    public UUID getStep(int step) {
        UUID stepUuid;
        try {
            stepUuid = path.get(step);
        } catch (Exception e) {
            return null;
        }
        return stepUuid;
    }

    public int getSize() {
        return path.size();
    }

    @Override
    public String toString() {
        String res = "";
        for (UUID id : path) {
            res = res.concat(id + ", ");
        }
        return res;
    }
}
